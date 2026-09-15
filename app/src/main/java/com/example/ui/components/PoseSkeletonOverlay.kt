package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.model.PoseLandmarkPoint
import com.example.pose.PoseLandmarkConstants
import com.example.ui.theme.PoseXCyan
import com.example.ui.theme.PoseXLavender
import com.example.ui.theme.PoseXNeonPink
import com.example.ui.theme.SkeletonMatchHigh
import com.example.ui.theme.SkeletonMatchLow
import com.example.ui.theme.SkeletonMatchMedium
import com.example.ui.theme.SkeletonReference

@Composable
fun PoseSkeletonOverlay(
    modifier: Modifier = Modifier,
    referenceLandmarks: Map<Int, PoseLandmarkPoint>?,
    detectedLandmarks: Map<Int, PoseLandmarkPoint>?,
    opacity: Float = 0.70f,
    scale: Float = 1.0f,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
    isMirrored: Boolean = false,
    matchScore: Int = 0,
    skeletonStyle: String = "Neon Violet"
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Draw Reference Pose (The Goal Guide Pose)
        if (referenceLandmarks != null && referenceLandmarks.isNotEmpty() && opacity > 0.05f) {
            val baseColor = when (skeletonStyle) {
                "Cyber Pink" -> PoseXNeonPink
                "Electric Mint" -> Color(0xFF2DD4BF)
                else -> PoseXLavender
            }

            val strokeWidthRef = 3.5.dp.toPx()
            val jointRadiusRef = 4.5.dp.toPx()

            // Pre-calculate transformed points for reference
            val transformedRef = referenceLandmarks.mapValues { (_, pt) ->
                var px = pt.x
                if (isMirrored) {
                    px = 1.0f - px
                }
                // Center-anchored scaling and translation
                val cx = 0.5f
                val cy = 0.5f
                val scaledX = cx + (px - cx) * scale + offsetX
                val scaledY = cy + (pt.y - cy) * scale + offsetY

                Offset(scaledX * w, scaledY * h)
            }

            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)

            // Draw connecting bones
            for ((startId, endId) in PoseLandmarkConstants.SKELETON_CONNECTIONS) {
                val start = transformedRef[startId]
                val end = transformedRef[endId]
                if (start != null && end != null) {
                    // Subtle outer glow
                    drawLine(
                        color = baseColor.copy(alpha = opacity * 0.25f),
                        start = start,
                        end = end,
                        strokeWidth = strokeWidthRef * 2.2f,
                        cap = StrokeCap.Round
                    )
                    // Sharp dashed guide line
                    drawLine(
                        color = baseColor.copy(alpha = opacity * 0.85f),
                        start = start,
                        end = end,
                        strokeWidth = strokeWidthRef,
                        cap = StrokeCap.Round,
                        pathEffect = dashEffect
                    )
                }
            }

            // Draw joint nodes
            for ((id, pos) in transformedRef) {
                val isTorsoOrHead = id == PoseLandmarkConstants.NOSE ||
                    id == PoseLandmarkConstants.LEFT_SHOULDER ||
                    id == PoseLandmarkConstants.RIGHT_SHOULDER ||
                    id == PoseLandmarkConstants.LEFT_HIP ||
                    id == PoseLandmarkConstants.RIGHT_HIP

                val currentRadius = if (isTorsoOrHead) jointRadiusRef * 1.2f else jointRadiusRef

                // Glow halo
                drawCircle(
                    color = baseColor.copy(alpha = opacity * 0.35f),
                    radius = currentRadius * 1.8f,
                    center = pos
                )
                // Solid core
                drawCircle(
                    color = Color.White.copy(alpha = opacity * 0.95f),
                    radius = currentRadius,
                    center = pos
                )
            }
        }

        // 2. Draw Live Detected Skeleton (Subtle, sleek, non-intrusive)
        if (detectedLandmarks != null && detectedLandmarks.isNotEmpty()) {
            val liveColor = when {
                matchScore >= 75 -> SkeletonMatchHigh
                matchScore >= 50 -> SkeletonMatchMedium
                else -> SkeletonMatchLow
            }

            val liveStrokeWidth = 3.dp.toPx()
            val liveJointRadius = 4.dp.toPx()

            val livePoints = detectedLandmarks.mapValues { (_, pt) ->
                Offset(pt.x * w, pt.y * h)
            }

            // Draw live bones
            for ((startId, endId) in PoseLandmarkConstants.SKELETON_CONNECTIONS) {
                val start = livePoints[startId]
                val end = livePoints[endId]
                if (start != null && end != null) {
                    // Soft glow
                    drawLine(
                        color = liveColor.copy(alpha = 0.35f),
                        start = start,
                        end = end,
                        strokeWidth = liveStrokeWidth * 2f,
                        cap = StrokeCap.Round
                    )
                    // Core line
                    drawLine(
                        color = liveColor.copy(alpha = 0.9f),
                        start = start,
                        end = end,
                        strokeWidth = liveStrokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw live joint nodes
            for ((_, pos) in livePoints) {
                drawCircle(
                    color = liveColor.copy(alpha = 0.4f),
                    radius = liveJointRadius * 1.6f,
                    center = pos
                )
                drawCircle(
                    color = Color.White,
                    radius = liveJointRadius * 0.7f,
                    center = pos
                )
            }
        }
    }
}
