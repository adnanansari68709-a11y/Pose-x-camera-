package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PoseViewModel
import com.example.ui.theme.PoseXCyan
import com.example.ui.theme.PoseXDarkBackground
import com.example.ui.theme.PoseXDarkSurface
import com.example.ui.theme.PoseXElectricViolet
import com.example.ui.theme.PoseXGlassBorder
import com.example.ui.theme.PoseXNeonGreen
import com.example.ui.theme.PoseXNeonPink
import com.example.ui.theme.PoseXTextPrimary
import com.example.ui.theme.PoseXTextSecondary

@Composable
fun SettingsScreen(
    viewModel: PoseViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PoseXDarkBackground)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PoseXDarkSurface)
                        .border(1.dp, PoseXGlassBorder, CircleShape)
                        .testTag("settings_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Camera Preferences Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "CAMERA & SKELETON",
                    color = PoseXCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Default Lens Facing (Front vs Rear)
                SettingsCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Start Camera With", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Default camera lens on open", color = PoseXTextSecondary, fontSize = 12.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LensChoicePill(
                                title = "Front",
                                isSelected = settings.isFrontCamera,
                                onClick = { viewModel.settingsRepository.updateFrontCamera(true) }
                            )
                            LensChoicePill(
                                title = "Rear",
                                isSelected = !settings.isFrontCamera,
                                onClick = { viewModel.settingsRepository.updateFrontCamera(false) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Skeleton Color Theme
                SettingsCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Skeleton Aesthetic Style", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Color theme of reference and match lines", color = PoseXTextSecondary, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Neon Violet", "Cyber Pink", "Electric Mint").forEach { style ->
                                val isSelected = settings.skeletonStyle == style
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PoseXElectricViolet else Color(0x22FFFFFF))
                                        .border(
                                            1.dp,
                                            if (isSelected) PoseXCyan else PoseXGlassBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.settingsRepository.updateSkeletonStyle(style) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = style,
                                        color = if (isSelected) Color.White else PoseXTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Overlay Opacity
                SettingsCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Reference Skeleton Opacity", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("${(settings.overlayOpacity * 100).toInt()}%", color = PoseXCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Slider(
                            value = settings.overlayOpacity,
                            onValueChange = { viewModel.settingsRepository.updateOverlayOpacity(it) },
                            valueRange = 0.2f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = PoseXCyan,
                                activeTrackColor = PoseXElectricViolet
                            )
                        )
                    }
                }
            }
        }

        // Feedback & Accessibility Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "FEEDBACK & HAPTICS",
                    color = PoseXCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Haptics Toggle
                SettingsCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Vibration & Haptics", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Tactile click on pose match & capture", color = PoseXTextSecondary, fontSize = 12.sp)
                        }

                        Switch(
                            checked = settings.hapticsEnabled,
                            onCheckedChange = { viewModel.settingsRepository.updateHaptics(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PoseXElectricViolet
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Audio Countdown Beep Toggle
                SettingsCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Countdown Audio Beep", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Play audible beeps during timer countdown", color = PoseXTextSecondary, fontSize = 12.sp)
                        }

                        Switch(
                            checked = settings.countdownSoundEnabled,
                            onCheckedChange = { viewModel.settingsRepository.updateCountdownSound(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PoseXElectricViolet
                            )
                        )
                    }
                }
            }
        }

        // Privacy & Offline Engine Info
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "100% On-Device Privacy",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Camera video frames are processed entirely on your phone using Google ML Kit. No video or biometric landmarks are ever sent to remote servers.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PoseXDarkSurface)
            .border(1.dp, PoseXGlassBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun LensChoicePill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PoseXElectricViolet else Color(0x33FFFFFF))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
