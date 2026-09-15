package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.camera.CameraCaptureManager
import com.example.ui.PoseViewModel
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.PoseMiniatureCanvas
import com.example.ui.components.PoseSkeletonOverlay
import com.example.ui.theme.PoseXCyan
import com.example.ui.theme.PoseXDarkBackground
import com.example.ui.theme.PoseXDarkSurface
import com.example.ui.theme.PoseXElectricViolet
import com.example.ui.theme.PoseXGlass
import com.example.ui.theme.PoseXGlassBorder
import com.example.ui.theme.PoseXLavender
import com.example.ui.theme.PoseXNeonGreen
import com.example.ui.theme.PoseXNeonPink
import com.example.ui.theme.PoseXTextMuted
import com.example.ui.theme.PoseXTextPrimary
import com.example.ui.theme.PoseXTextSecondary
import com.example.ui.theme.SkeletonMatchHigh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    viewModel: PoseViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPreview: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPoses: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val selectedPose by viewModel.selectedPose.collectAsState()
    val detectedLandmarks by viewModel.detectedLandmarks.collectAsState()
    val matchResult by viewModel.matchResult.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val countdownRemaining by viewModel.countdownRemaining.collectAsState()
    val lastCapturedPhoto by viewModel.lastCapturedPhoto.collectAsState()
    val capturedPhotoUri = lastCapturedPhoto?.uriString

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required for pose matching", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Camera Manager
    val cameraManager = remember {
        CameraCaptureManager(context, viewModel.poseDetectorEngine)
    }

    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
    var isShutterFlashVisible by remember { mutableStateOf(false) }
    var showAdjustSheet by remember { mutableStateOf(false) }
    var showPoseInfoSheet by remember { mutableStateOf(false) }
    var isTorchActive by remember { mutableStateOf(false) }
    var currentZoomLevel by remember { mutableFloatStateOf(1.0f) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraManager.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_screen")
    ) {
        if (!hasCameraPermission) {
            // Permission Rationale UI - Clean and professional
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Camera Access Needed",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "POSEX uses live camera feed and ML Kit to compare your pose in real-time.",
                        color = PoseXTextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PoseXElectricViolet,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Grant Camera Access", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        } else {
            // 1. FULL SCREEN LIVE CAMERA FEED
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }.also { preview ->
                        previewViewRef = preview
                        cameraManager.initialize(
                            lifecycleOwner = lifecycleOwner,
                            previewView = preview,
                            startFrontCamera = settings.isFrontCamera,
                            onPoseDetected = { landmarks, _ ->
                                viewModel.onLivePoseDetected(landmarks)
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // 2. POSE SKELETON OVERLAY (Natural proportions, semi-transparent, no green boxes)
            PoseSkeletonOverlay(
                modifier = Modifier.fillMaxSize(),
                referenceLandmarks = selectedPose?.landmarks,
                detectedLandmarks = detectedLandmarks,
                opacity = settings.overlayOpacity,
                scale = settings.overlayScale,
                offsetX = settings.overlayOffsetX,
                offsetY = settings.overlayOffsetY,
                isMirrored = settings.isMirrored,
                matchScore = matchResult.overallScore,
                skeletonStyle = settings.skeletonStyle
            )

            // 3. SHUTTER FLASH ANIMATION OVERLAY
            AnimatedVisibility(
                visible = isShutterFlashVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }

            // 4. TOP CONTROLS BAR: Back | Pose Name Pill | Flash | Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x99000000))
                        .testTag("camera_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Center Pose Pill: Tapping opens Pose Switcher / Details
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xBB12101D))
                        .border(1.dp, PoseXGlassBorder, RoundedCornerShape(24.dp))
                        .clickable { showPoseInfoSheet = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("active_pose_pill")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedPose?.title ?: "Select Pose",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${selectedPose?.category ?: "Library"}",
                            color = PoseXCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Flash & Settings Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Flash / Torch Toggle
                    IconButton(
                        onClick = {
                            cameraManager.toggleTorch { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                            isTorchActive = cameraManager.isTorchOn
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000))
                            .testTag("flash_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isTorchActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Flash",
                            tint = if (isTorchActive) PoseXCyan else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000))
                            .testTag("camera_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Camera Settings",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 5. POSE MATCH HUD (Floating below the top bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 104.dp, start = 16.dp, end = 16.dp)
            ) {
                MatchScoreBadge(
                    modifier = Modifier.fillMaxWidth(),
                    matchResult = matchResult
                )
            }

            // 6. GIANT COUNTDOWN OVERLAY
            if (countdownRemaining != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x55000000))
                ) {
                    Text(
                        text = "${countdownRemaining}",
                        color = PoseXCyan,
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // 7. BOTTOM CAMERA INTERFACE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp, start = 20.dp, end = 20.dp)
            ) {
                // Secondary Controls Pill: Timer | Opacity | Mirror | Zoom
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color(0xAA12101D))
                        .border(1.dp, PoseXGlassBorder, RoundedCornerShape(30.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Timer Cycle (Off -> 3s -> 5s -> 10s)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                val nextTimer = when (settings.timerSeconds) {
                                    0 -> 3
                                    3 -> 5
                                    5 -> 10
                                    else -> 0
                                }
                                viewModel.settingsRepository.updateTimerSeconds(nextTimer)
                            }
                            .padding(4.dp)
                            .testTag("camera_timer_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = if (settings.timerSeconds > 0) PoseXCyan else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (settings.timerSeconds == 0) "OFF" else "${settings.timerSeconds}s",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Opacity / Layers Adjust Sheet
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showAdjustSheet = true }
                            .padding(4.dp)
                            .testTag("camera_opacity_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Adjust Opacity",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${(settings.overlayOpacity * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Mirror Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                viewModel.settingsRepository.updateMirrored(!settings.isMirrored)
                            }
                            .padding(4.dp)
                            .testTag("camera_mirror_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = "Mirror",
                            tint = if (settings.isMirrored) PoseXCyan else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (settings.isMirrored) "Mirrored" else "Mirror",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Zoom 1x / 2x Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                currentZoomLevel = if (currentZoomLevel == 1.0f) 2.0f else 1.0f
                                cameraManager.setZoom(currentZoomLevel)
                            }
                            .padding(4.dp)
                            .testTag("camera_zoom_btn")
                    ) {
                        Text(
                            text = "${currentZoomLevel.toInt()}x",
                            color = if (currentZoomLevel > 1.0f) PoseXCyan else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Bottom Row: Gallery Thumbnail | Shutter Button | Camera Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Gallery / Recent Capture Thumbnail
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            .clickable {
                                if (capturedPhotoUri != null) {
                                    onNavigateToPreview()
                                } else {
                                    onNavigateToPoses()
                                }
                            }
                            .testTag("preview_gallery_btn")
                    ) {
                        if (capturedPhotoUri != null) {
                            AsyncImage(
                                model = capturedPhotoUri,
                                contentDescription = "Captured Photo",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Pose Library",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Center: Large Circular Shutter Button (82dp touch-friendly)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(82.dp)
                            .clip(CircleShape)
                            .background(Color(0x339333EA))
                            .border(
                                3.dp,
                                if (matchResult.isMatched) SkeletonMatchHigh else Color.White,
                                CircleShape
                            )
                            .clickable {
                                viewModel.startCaptureCountdown {
                                    isShutterFlashVisible = true
                                    cameraManager.capturePhoto(
                                        onSuccess = { uri ->
                                            isShutterFlashVisible = false
                                            viewModel.onPhotoCaptured(uri)
                                            Toast.makeText(context, "Photo captured!", Toast.LENGTH_SHORT).show()
                                            onNavigateToPreview()
                                        },
                                        onError = { error ->
                                            isShutterFlashVisible = false
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                            .testTag("shutter_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (matchResult.isMatched) SkeletonMatchHigh else Color.White
                                )
                        )
                    }

                    // Right: Camera Switch / Flip Button
                    IconButton(
                        onClick = {
                            val preview = previewViewRef
                            if (preview != null) {
                                cameraManager.switchCamera(
                                    lifecycleOwner = lifecycleOwner,
                                    previewView = preview,
                                    onPoseDetected = { lm, _ -> viewModel.onLivePoseDetected(lm) },
                                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                                )
                                viewModel.settingsRepository.updateFrontCamera(cameraManager.isFrontCamera)
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            .testTag("flip_camera_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // ADJUST OVERLAY BOTTOM SHEET
        if (showAdjustSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAdjustSheet = false },
                containerColor = PoseXDarkSurface,
                scrimColor = Color(0x99000000),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Adjust Pose Guide",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showAdjustSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = PoseXTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Opacity Slider
                    Text(
                        text = "Opacity: ${(settings.overlayOpacity * 100).toInt()}%",
                        color = PoseXTextSecondary,
                        fontSize = 14.sp
                    )
                    Slider(
                        value = settings.overlayOpacity,
                        onValueChange = { viewModel.settingsRepository.updateOverlayOpacity(it) },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = PoseXElectricViolet,
                            activeTrackColor = PoseXElectricViolet,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scale Slider
                    Text(
                        text = "Scale: ${(settings.overlayScale * 100).toInt()}%",
                        color = PoseXTextSecondary,
                        fontSize = 14.sp
                    )
                    Slider(
                        value = settings.overlayScale,
                        onValueChange = { viewModel.settingsRepository.updateOverlayTransform(it, settings.overlayOffsetX, settings.overlayOffsetY) },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = PoseXCyan,
                            activeTrackColor = PoseXCyan,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reset Adjustments Button
                    Button(
                        onClick = {
                            viewModel.settingsRepository.updateOverlayOpacity(0.70f)
                            viewModel.settingsRepository.resetOverlayTransform()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x22FFFFFF),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset Adjustments", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // POSE INFO & SWITCHER BOTTOM SHEET
        if (showPoseInfoSheet && selectedPose != null) {
            val pose = selectedPose!!
            ModalBottomSheet(
                onDismissRequest = { showPoseInfoSheet = false },
                containerColor = PoseXDarkSurface,
                scrimColor = Color(0x99000000),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = pose.title,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${pose.category} • ${pose.difficulty}",
                                color = PoseXCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(onClick = { showPoseInfoSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = PoseXTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = pose.description,
                        color = PoseXTextSecondary,
                        fontSize = 14.sp
                    )

                    if (pose.instructions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pose Instructions",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        pose.instructions.forEachIndexed { index, instruction ->
                            Text(
                                text = "${index + 1}. $instruction",
                                color = PoseXTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showPoseInfoSheet = false
                            onNavigateToPoses()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PoseXElectricViolet,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Browse Other Poses", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
