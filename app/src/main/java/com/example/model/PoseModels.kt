package com.example.model

/**
 * Normalized 3D landmark point (x, y, z in [0..1] space, likelihood [0..1]).
 */
data class PoseLandmarkPoint(
    val x: Float,
    val y: Float,
    val z: Float = 0f,
    val likelihood: Float = 1f
)

/**
 * High-level body part feedback item.
 */
enum class MatchStatus {
    MATCHED,
    ADJUST,
    MISSING
}

data class BodyPartFeedback(
    val partName: String,
    val status: MatchStatus,
    val message: String
)

/**
 * Result of pose matching engine comparison.
 */
data class PoseMatchResult(
    val overallScore: Int,
    val isMatched: Boolean,
    val bodyPartGuidance: List<BodyPartFeedback>,
    val primarySuggestion: String,
    val detectedLandmarkCount: Int = 0
)

/**
 * Template pose model for the local pose library.
 */
data class PoseTemplate(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String, // "Easy", "Medium", "Pro"
    val description: String,
    val instructions: List<String>,
    val landmarks: Map<Int, PoseLandmarkPoint>,
    val tags: List<String> = emptyList(),
    val isFullBody: Boolean = true,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)

/**
 * Settings data model.
 */
data class CameraSettings(
    val isFrontCamera: Boolean = true,
    val flashMode: Int = 0, // 0 = OFF, 1 = ON, 2 = AUTO
    val timerSeconds: Int = 3, // 0, 3, 5, 10
    val overlayOpacity: Float = 0.75f,
    val overlayScale: Float = 1.0f,
    val overlayOffsetX: Float = 0f,
    val overlayOffsetY: Float = 0f,
    val isMirrored: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val countdownSoundEnabled: Boolean = true,
    val skeletonStyle: String = "Neon Violet"
)

/**
 * Captured photo data with pose metadata.
 */
data class CapturedPhoto(
    val uriString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val matchScore: Int = 0,
    val poseTitle: String = "Free Pose"
)
