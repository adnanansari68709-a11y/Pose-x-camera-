package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BodyPartFeedback
import com.example.model.MatchStatus
import com.example.model.PoseMatchResult
import com.example.ui.theme.PoseXCyan
import com.example.ui.theme.PoseXElectricViolet
import com.example.ui.theme.PoseXGlass
import com.example.ui.theme.PoseXGlassBorder
import com.example.ui.theme.PoseXLavender
import com.example.ui.theme.PoseXTextMuted
import com.example.ui.theme.PoseXTextPrimary
import com.example.ui.theme.PoseXTextSecondary
import com.example.ui.theme.SkeletonMatchHigh
import com.example.ui.theme.SkeletonMatchLow
import com.example.ui.theme.SkeletonMatchMedium

@Composable
fun MatchScoreBadge(
    modifier: Modifier = Modifier,
    matchResult: PoseMatchResult
) {
    var isExpanded by remember { mutableStateOf(false) }

    val scoreProgress by animateFloatAsState(
        targetValue = (matchResult.overallScore / 100f).coerceIn(0f, 1f),
        animationSpec = spring(),
        label = "score_progress"
    )

    val scoreColor by animateColorAsState(
        targetValue = when {
            matchResult.overallScore >= 75 -> SkeletonMatchHigh
            matchResult.overallScore >= 50 -> SkeletonMatchMedium
            else -> SkeletonMatchLow
        },
        animationSpec = spring(),
        label = "score_color"
    )

    Box(
        modifier = modifier
            .testTag("match_score_hud")
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xDD12101D))
            .border(1.dp, PoseXGlassBorder, RoundedCornerShape(20.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column {
            // Header Row: Score Label, Score Percentage, and Expand Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Small circular ring or dot
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(36.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { scoreProgress },
                            modifier = Modifier.size(36.dp),
                            color = scoreColor,
                            trackColor = Color(0x22FFFFFF),
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "${matchResult.overallScore}%",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (matchResult.isMatched) "POSE MATCH LOCKED" else "POSE MATCH",
                                color = scoreColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                            if (matchResult.isMatched) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SkeletonMatchHigh)
                                )
                            }
                        }

                        Text(
                            text = matchResult.primarySuggestion,
                            color = PoseXTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle Guidance Details",
                    tint = PoseXTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Quick Guidance Checklist
            if (matchResult.bodyPartGuidance.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    // Show top 3 in compact mode, all when expanded
                    val partsToShow = if (isExpanded) {
                        matchResult.bodyPartGuidance
                    } else {
                        matchResult.bodyPartGuidance.take(3)
                    }

                    partsToShow.forEach { feedback ->
                        CleanGuidanceRow(feedback = feedback)
                    }
                }
            }
        }
    }
}

@Composable
private fun CleanGuidanceRow(feedback: BodyPartFeedback) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when (feedback.status) {
                MatchStatus.MATCHED -> {
                    Text(
                        text = "✓",
                        color = SkeletonMatchHigh,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(16.dp)
                    )
                }
                MatchStatus.ADJUST -> {
                    Text(
                        text = "⚠",
                        color = SkeletonMatchMedium,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(16.dp)
                    )
                }
                MatchStatus.MISSING -> {
                    Text(
                        text = "•",
                        color = PoseXTextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(16.dp)
                    )
                }
            }

            Text(
                text = feedback.partName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = feedback.message,
            color = when (feedback.status) {
                MatchStatus.MATCHED -> SkeletonMatchHigh
                MatchStatus.ADJUST -> SkeletonMatchMedium
                MatchStatus.MISSING -> PoseXTextMuted
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
