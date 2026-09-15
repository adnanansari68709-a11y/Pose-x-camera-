package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PoseTemplate
import com.example.pose.PoseLandmarkConstants
import com.example.ui.theme.PoseXCyan
import com.example.ui.theme.PoseXDarkSurface
import com.example.ui.theme.PoseXElectricViolet
import com.example.ui.theme.PoseXGlassBorder
import com.example.ui.theme.PoseXLavender
import com.example.ui.theme.PoseXNeonGreen
import com.example.ui.theme.PoseXNeonPink
import com.example.ui.theme.PoseXTextPrimary
import com.example.ui.theme.PoseXTextSecondary

@Composable
fun PoseCard(
    modifier: Modifier = Modifier,
    pose: PoseTemplate,
    onPoseClick: (PoseTemplate) -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    val heartColor by animateColorAsState(
        targetValue = if (pose.isFavorite) PoseXNeonPink else PoseXTextSecondary,
        label = "heart_color"
    )

    Box(
        modifier = modifier
            .testTag("pose_card_${pose.id}")
            .clip(RoundedCornerShape(22.dp))
            .background(PoseXDarkSurface)
            .border(1.dp, PoseXGlassBorder, RoundedCornerShape(22.dp))
            .clickable { onPoseClick(pose) }
            .padding(14.dp)
    ) {
        Column {
            // Skeleton Visual Thumbnail
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0C0A14))
            ) {
                // Miniature skeleton render
                PoseMiniatureCanvas(
                    pose = pose,
                    modifier = Modifier
                        .size(110.dp)
                        .padding(6.dp)
                )

                // Favorite Heart Button in top-right corner
                IconButton(
                    onClick = { onFavoriteToggle(pose.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x77000000))
                        .testTag("favorite_btn_${pose.id}")
                ) {
                    Icon(
                        imageVector = if (pose.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = heartColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Difficulty Chip in top-left corner
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x99181424))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = pose.difficulty.uppercase(),
                        color = when (pose.difficulty) {
                            "Easy" -> PoseXNeonGreen
                            "Pro" -> PoseXLavender
                            else -> PoseXCyan
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pose Title & Category
            Text(
                text = pose.title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${pose.category} • ${pose.estimatedFullBodyText()}",
                color = PoseXTextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Try Pose Action Button
            Button(
                onClick = { onPoseClick(pose) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x339333EA),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("try_pose_${pose.id}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = PoseXLavender,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Try Pose",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun PoseTemplate.estimatedFullBodyText(): String {
    val hasLegs = landmarks.containsKey(PoseLandmarkConstants.LEFT_ANKLE) ||
        landmarks.containsKey(PoseLandmarkConstants.RIGHT_ANKLE)
    return if (hasLegs) "Full Body" else "Upper Body"
}

@Composable
fun PoseMiniatureCanvas(
    pose: PoseTemplate,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()
        val jointR = 2.5.dp.toPx()

        val points = pose.landmarks.mapValues { (_, pt) ->
            Offset(pt.x * w, pt.y * h)
        }

        // Draw bone lines
        for ((startId, endId) in PoseLandmarkConstants.SKELETON_CONNECTIONS) {
            val start = points[startId]
            val end = points[endId]
            if (start != null && end != null) {
                drawLine(
                    color = PoseXLavender.copy(alpha = 0.85f),
                    start = start,
                    end = end,
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw joint nodes
        for ((_, pos) in points) {
            drawCircle(
                color = Color.White,
                radius = jointR,
                center = pos
            )
        }
    }
}
