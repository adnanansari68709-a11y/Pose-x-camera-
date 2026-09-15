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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PoseTemplate
import com.example.ui.PoseViewModel
import com.example.ui.components.PoseCard
import com.example.ui.theme.PoseXCyan
import com.example.ui.theme.PoseXDarkBackground
import com.example.ui.theme.PoseXDarkSurface
import com.example.ui.theme.PoseXElectricViolet
import com.example.ui.theme.PoseXGlassBorder
import com.example.ui.theme.PoseXLavender
import com.example.ui.theme.PoseXNeonGreen
import com.example.ui.theme.PoseXNeonPink
import com.example.ui.theme.PoseXTextPrimary
import com.example.ui.theme.PoseXTextSecondary

@Composable
fun HomeScreen(
    viewModel: PoseViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToPoses: (String?) -> Unit,
    onNavigateToSettings: () -> Unit,
    onSelectPoseAndCamera: (PoseTemplate) -> Unit
) {
    val allPoses by viewModel.allPoses.collectAsState()
    val recentPoses by viewModel.recentPoses.collectAsState()
    val favoritePoses by viewModel.favoritePoses.collectAsState()
    val recommendedPoses by viewModel.recommendedPoses.collectAsState()

    val requestedCategories = listOf(
        "Standing",
        "Sitting",
        "Mirror",
        "Street",
        "Fashion",
        "Casual",
        "Travel",
        "Indoor",
        "Outdoor"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PoseXDarkBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // 1. BRAND HEADER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "POSEX",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        modifier = Modifier.testTag("app_header_title")
                    )
                    Text(
                        text = "AI POSE CAMERA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PoseXLavender,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.5.sp
                        )
                    )
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PoseXDarkSurface)
                        .border(1.dp, PoseXGlassBorder, CircleShape)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = PoseXTextPrimary
                    )
                }
            }
        }

        // 2. [ START AI CAMERA ] HERO CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF23113D), Color(0xFF141121), Color(0xFF2A0D2A))
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(PoseXLavender.copy(alpha = 0.6f), Color.Transparent)),
                        RoundedCornerShape(26.dp)
                    )
                    .clickable { onNavigateToCamera() }
                    .padding(22.dp)
                    .testTag("start_camera_hero")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PoseXNeonGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REAL-TIME SKELETON GUIDANCE",
                                color = PoseXLavender,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Start AI Camera",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Match trending pose outlines with instant alignment feedback.",
                            color = PoseXTextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Button(
                            onClick = onNavigateToCamera,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PoseXElectricViolet,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Launch Camera",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Stylized camera aperture ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0x339333EA))
                            .border(1.5.dp, PoseXLavender, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // 3. TRENDING POSES
        item {
            Column(modifier = Modifier.padding(top = 28.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Trending Poses",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x339333EA))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "HOT",
                                color = PoseXLavender,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Text(
                        text = "See All",
                        color = PoseXLavender,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onNavigateToPoses(null) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                val displayPoses = if (recommendedPoses.isNotEmpty()) recommendedPoses else allPoses.take(6)

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(displayPoses) { pose ->
                        PoseCard(
                            modifier = Modifier.width(195.dp),
                            pose = pose,
                            onPoseClick = { onSelectPoseAndCamera(pose) },
                            onFavoriteToggle = { viewModel.toggleFavorite(it) }
                        )
                    }
                }
            }
        }

        // 4. CATEGORIES
        item {
            Column(modifier = Modifier.padding(top = 28.dp)) {
                Text(
                    text = "Categories",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(requestedCategories) { category ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(PoseXDarkSurface)
                                .border(1.dp, PoseXGlassBorder, RoundedCornerShape(14.dp))
                                .clickable { onNavigateToPoses(category) }
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                                .testTag("category_chip_$category")
                        ) {
                            Text(
                                text = category,
                                color = PoseXTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 5. RECENTLY USED
        if (recentPoses.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(top = 28.dp)) {
                    Text(
                        text = "Recently Used",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(recentPoses) { pose ->
                            PoseCard(
                                modifier = Modifier.width(195.dp),
                                pose = pose,
                                onPoseClick = { onSelectPoseAndCamera(pose) },
                                onFavoriteToggle = { viewModel.toggleFavorite(it) }
                            )
                        }
                    }
                }
            }
        }

        // 6. FAVORITES
        if (favoritePoses.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(top = 28.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your Favorites",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${favoritePoses.size} saved",
                            color = PoseXTextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(favoritePoses) { pose ->
                            PoseCard(
                                modifier = Modifier.width(195.dp),
                                pose = pose,
                                onPoseClick = { onSelectPoseAndCamera(pose) },
                                onFavoriteToggle = { viewModel.toggleFavorite(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
