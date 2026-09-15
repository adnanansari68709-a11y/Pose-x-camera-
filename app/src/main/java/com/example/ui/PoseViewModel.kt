package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.camera.HapticAndSoundHelper
import com.example.data.CaptureHistoryEntity
import com.example.data.PoseDatabase
import com.example.data.PoseRepository
import com.example.data.SettingsRepository
import com.example.model.CameraSettings
import com.example.model.CapturedPhoto
import com.example.model.PoseLandmarkPoint
import com.example.model.PoseMatchResult
import com.example.model.PoseTemplate
import com.example.pose.PoseDetectorEngine
import com.example.pose.PoseMatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PoseViewModel(application: Application) : AndroidViewModel(application) {

    private val poseDao = PoseDatabase.getDatabase(application).poseDao()
    private val poseRepository = PoseRepository(poseDao)
    val settingsRepository = SettingsRepository(application)
    val poseDetectorEngine = PoseDetectorEngine()
    private val poseMatcher = PoseMatcher()
    val hapticAndSoundHelper = HapticAndSoundHelper(application)

    // Reactive Data Sources
    val allPoses: StateFlow<List<PoseTemplate>> = poseRepository.getAllPoses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritePoses: StateFlow<List<PoseTemplate>> = poseRepository.getFavoritePoses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentPoses: StateFlow<List<PoseTemplate>> = poseRepository.getRecentPoses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCapturesCount: StateFlow<Int> = poseRepository.getTotalCapturesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val captureHistory: StateFlow<List<CaptureHistoryEntity>> = poseRepository.getCaptureHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<CameraSettings> = settingsRepository.settings

    // Camera & Pose State
    private val _selectedPose = MutableStateFlow<PoseTemplate?>(null)
    val selectedPose: StateFlow<PoseTemplate?> = _selectedPose.asStateFlow()

    private val _detectedLandmarks = MutableStateFlow<Map<Int, PoseLandmarkPoint>?>(null)
    val detectedLandmarks: StateFlow<Map<Int, PoseLandmarkPoint>?> = _detectedLandmarks.asStateFlow()

    private val _matchResult = MutableStateFlow(
        PoseMatchResult(
            overallScore = 0,
            isMatched = false,
            bodyPartGuidance = emptyList(),
            primarySuggestion = "Move into camera frame"
        )
    )
    val matchResult: StateFlow<PoseMatchResult> = _matchResult.asStateFlow()

    private val _recommendedPoses = MutableStateFlow<List<PoseTemplate>>(emptyList())
    val recommendedPoses: StateFlow<List<PoseTemplate>> = _recommendedPoses.asStateFlow()

    // Countdown Timer State
    private val _countdownRemaining = MutableStateFlow<Int?>(null)
    val countdownRemaining: StateFlow<Int?> = _countdownRemaining.asStateFlow()

    // Last Captured Photo
    private val _lastCapturedPhoto = MutableStateFlow<CapturedPhoto?>(null)
    val lastCapturedPhoto: StateFlow<CapturedPhoto?> = _lastCapturedPhoto.asStateFlow()

    // Status / Feedback message
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var countdownJob: Job? = null

    init {
        // Set default initial pose once allPoses emits
        viewModelScope.launch {
            allPoses.collectLatest { poses ->
                if (_selectedPose.value == null && poses.isNotEmpty()) {
                    _selectedPose.value = poses.first()
                }
                refreshRecommendations()
            }
        }
    }

    fun selectPose(pose: PoseTemplate) {
        _selectedPose.value = pose
        poseMatcher.reset()
        viewModelScope.launch {
            poseRepository.markPoseUsed(pose.id)
            refreshRecommendations()
        }
    }

    fun toggleFavorite(poseId: String) {
        viewModelScope.launch {
            poseRepository.toggleFavorite(poseId)
            if (settings.value.hapticsEnabled) {
                hapticAndSoundHelper.triggerTickHaptic()
            }
        }
    }

    fun onLivePoseDetected(landmarks: Map<Int, PoseLandmarkPoint>?) {
        _detectedLandmarks.value = landmarks
        val activePose = _selectedPose.value
        if (activePose != null && activePose.landmarks.isNotEmpty()) {
            val result = poseMatcher.match(
                detected = landmarks,
                reference = activePose.landmarks,
                isFullBodyRequired = activePose.isFullBody
            )
            _matchResult.value = result
        }
    }

    fun startCaptureCountdown(onTriggerCapture: () -> Unit) {
        val timer = settings.value.timerSeconds
        if (timer <= 0) {
            onTriggerCapture()
            return
        }

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in timer downTo 1) {
                _countdownRemaining.value = i
                if (settings.value.countdownSoundEnabled) {
                    hapticAndSoundHelper.playCountdownBeep(isFinal = (i == 1))
                }
                if (settings.value.hapticsEnabled) {
                    hapticAndSoundHelper.triggerTickHaptic()
                }
                delay(1000)
            }
            _countdownRemaining.value = null
            if (settings.value.hapticsEnabled) {
                hapticAndSoundHelper.triggerSnapHaptic()
            }
            onTriggerCapture()
        }
    }

    fun cancelCountdown() {
        countdownJob?.cancel()
        _countdownRemaining.value = null
    }

    fun onPhotoCaptured(uri: Uri) {
        val pose = _selectedPose.value
        val score = _matchResult.value.overallScore
        val title = pose?.title ?: "Free Pose"

        val photo = CapturedPhoto(
            uriString = uri.toString(),
            matchScore = score,
            poseTitle = title
        )
        _lastCapturedPhoto.value = photo

        viewModelScope.launch {
            poseRepository.saveCapture(uri.toString(), title, score)
        }
    }

    fun extractPoseFromCustomImage(
        bitmap: Bitmap,
        title: String,
        onSuccess: (PoseTemplate) -> Unit,
        onError: (String) -> Unit
    ) {
        poseDetectorEngine.detectPoseFromBitmap(bitmap) { landmarks, error ->
            if (error != null) {
                onError(error)
                _statusMessage.value = error
            } else if (landmarks != null) {
                val newPose = poseRepository.addCustomPose(title, landmarks)
                selectPose(newPose)
                onSuccess(newPose)
                _statusMessage.value = "Custom pose created!"
            }
        }
    }

    fun refreshRecommendations(category: String = "All") {
        viewModelScope.launch {
            val list = poseRepository.getRecommendations(
                currentCategory = category,
                isFrontCamera = settings.value.isFrontCamera,
                isFullBodyOnly = false
            )
            _recommendedPoses.value = list
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        poseDetectorEngine.close()
        hapticAndSoundHelper.release()
    }
}
