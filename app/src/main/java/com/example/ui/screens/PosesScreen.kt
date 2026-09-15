package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PoseTemplate
import com.example.pose.PoseLibraryData
import com.example.ui.PoseViewModel
import com.example.ui.components.PoseCard
import com.example.ui.components.PoseMiniatureCanvas
import com.example.ui.theme.PoseXCyan
import com.example.ui.theme.PoseXDarkBackground
import com.example.ui.theme.PoseXDarkSurface
import com.example.ui.theme.PoseXElectricViolet
import com.example.ui.theme.PoseXGlassBorder
import com.example.ui.theme.PoseXLavender
import com.example.ui.theme.PoseXNeonGreen
import com.example.ui.theme.PoseXNeonPink
import com.example.ui.theme.PoseXTextMuted
import com.example.ui.theme.PoseXTextPrimary
import com.example.ui.theme.PoseXTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosesScreen(
    viewModel: PoseViewModel,
    initialCategory: String? = null,
    onSelectPoseAndCamera: (PoseTemplate) -> Unit
) {
    val context = LocalContext.current
    val allPoses by viewModel.allPoses.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory ?: "All") }
    var isExtractingPhoto by remember { mutableStateOf(false) }

    // Selected pose for the detail bottom sheet
    var activePoseDetail by remember { mutableStateOf<PoseTemplate?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            isExtractingPhoto = true
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    viewModel.extractPoseFromCustomImage(
                        bitmap = bitmap,
                        title = "Custom Photo Pose",
                        onSuccess = { extractedPose ->
                            isExtractingPhoto = false
                            Toast.makeText(context, "Pose extracted! Opening camera...", Toast.LENGTH_SHORT).show()
                            onSelectPoseAndCamera(extractedPose)
                        },
                        onError = { error ->
                            isExtractingPhoto = false
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    isExtractingPhoto = false
                    Toast.makeText(context, "Could not load selected photo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                isExtractingPhoto = false
                Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Filter poses by search query and category
    val filteredPoses = allPoses.filter { pose ->
        val matchesCategory = selectedCategory == "All" ||
            pose.category.equals(selectedCategory, ignoreCase = true) ||
            pose.tags.any { it.equals(selectedCategory, ignoreCase = true) }

        val matchesSearch = searchQuery.isBlank() ||
            pose.title.contains(searchQuery, ignoreCase = true) ||
            pose.description.contains(searchQuery, ignoreCase = true) ||
            pose.category.contains(searchQuery, ignoreCase = true) ||
            pose.tags.any { it.contains(searchQuery, ignoreCase = true) }

        matchesCategory && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PoseXDarkBackground)
            .testTag("poses_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Title and "Import Photo"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Pose Library",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${filteredPoses.size} aesthetic poses",
                        color = PoseXTextSecondary,
                        fontSize = 13.sp
                    )
                }

                // Reference Photo Mode: Custom Pose Import Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x339333EA))
                        .border(1.dp, PoseXLavender, RoundedCornerShape(14.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("import_custom_pose_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Import Photo",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search poses (e.g. mirror, street, standing)", color = PoseXTextMuted, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PoseXTextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = PoseXTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PoseXDarkSurface,
                    unfocusedContainerColor = PoseXDarkSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = PoseXLavender,
                    unfocusedIndicatorColor = PoseXGlassBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("search_text_field")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PoseLibraryData.CATEGORIES) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PoseXElectricViolet else PoseXDarkSurface)
                            .border(
                                1.dp,
                                if (isSelected) PoseXLavender else PoseXGlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("filter_chip_$category")
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else PoseXTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pose Grid
            if (filteredPoses.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No poses found",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try searching another term or import a custom photo",
                            color = PoseXTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPoses, key = { it.id }) { pose ->
                        PoseCard(
                            pose = pose,
                            onPoseClick = {
                                activePoseDetail = pose
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite(it) }
                        )
                    }
                }
            }
        }

        // POSE DETAIL BOTTOM SHEET (Tapping a pose shows details, instructions & "Try this pose" button)
        if (activePoseDetail != null) {
            val pose = activePoseDetail!!
            ModalBottomSheet(
                onDismissRequest = { activePoseDetail = null },
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
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${pose.category} • ${pose.difficulty}",
                                color = PoseXCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(onClick = { activePoseDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = PoseXTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Miniature Skeleton Canvas
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0C0A14))
                            .border(1.dp, PoseXGlassBorder, RoundedCornerShape(16.dp))
                    ) {
                        PoseMiniatureCanvas(
                            pose = pose,
                            modifier = Modifier
                                .size(160.dp)
                                .padding(8.dp)
                        )
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
                            text = "How to Pose",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        pose.instructions.forEachIndexed { index, inst ->
                            Text(
                                text = "${index + 1}. $inst",
                                color = PoseXTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Prominent "Try this pose" Button
                    Button(
                        onClick = {
                            activePoseDetail = null
                            onSelectPoseAndCamera(pose)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PoseXElectricViolet,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("try_this_pose_btn")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Try this pose", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Photo Extraction Loading Overlay
        if (isExtractingPhoto) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PoseXLavender, strokeWidth = 3.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Extracting pose landmarks...",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
