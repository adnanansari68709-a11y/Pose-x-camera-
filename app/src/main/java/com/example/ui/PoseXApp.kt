package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.AppDestination
import com.example.ui.navigation.selectedIcon
import com.example.ui.navigation.unselectedIcon
import com.example.ui.screens.CameraScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PosesScreen
import com.example.ui.screens.PreviewScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.PoseXCyan
import com.example.ui.theme.PoseXDarkBackground
import com.example.ui.theme.PoseXDarkSurface
import com.example.ui.theme.PoseXElectricViolet
import com.example.ui.theme.PoseXGlassBorder
import com.example.ui.theme.PoseXLavender
import com.example.ui.theme.PoseXTextMuted
import com.example.ui.theme.PoseXTextPrimary
import com.example.ui.theme.PoseXTextSecondary

@Composable
fun PoseXApp(
    viewModel: PoseViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom navigation on full-screen camera and preview
    val isBottomBarVisible = currentRoute != AppDestination.CAMERA.route &&
        currentRoute != AppDestination.PREVIEW.route &&
        currentRoute != AppDestination.SETTINGS.route

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PoseXDarkBackground),
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                PoseXBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.HOME.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isBottomBarVisible) 0.dp else 0.dp)
        ) {
            composable(AppDestination.HOME.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = { navController.navigate(AppDestination.CAMERA.route) },
                    onNavigateToPoses = { category ->
                        navController.navigate(AppDestination.POSES.route)
                    },
                    onNavigateToSettings = { navController.navigate(AppDestination.SETTINGS.route) },
                    onSelectPoseAndCamera = { pose ->
                        viewModel.selectPose(pose)
                        navController.navigate(AppDestination.CAMERA.route)
                    }
                )
            }

            composable(AppDestination.POSES.route) {
                PosesScreen(
                    viewModel = viewModel,
                    onSelectPoseAndCamera = { pose ->
                        viewModel.selectPose(pose)
                        navController.navigate(AppDestination.CAMERA.route)
                    }
                )
            }

            composable(AppDestination.CAMERA.route) {
                CameraScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(AppDestination.HOME.route)
                        }
                    },
                    onNavigateToPreview = {
                        navController.navigate(AppDestination.PREVIEW.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(AppDestination.SETTINGS.route)
                    },
                    onNavigateToPoses = {
                        navController.navigate(AppDestination.POSES.route)
                    }
                )
            }

            composable(AppDestination.FAVORITES.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onNavigateToPoses = { navController.navigate(AppDestination.POSES.route) },
                    onSelectPoseAndCamera = { pose ->
                        viewModel.selectPose(pose)
                        navController.navigate(AppDestination.CAMERA.route)
                    }
                )
            }

            composable(AppDestination.PROFILE.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { navController.navigate(AppDestination.SETTINGS.route) },
                    onNavigateToFavorites = { navController.navigate(AppDestination.FAVORITES.route) }
                )
            }

            composable(AppDestination.PREVIEW.route) {
                PreviewScreen(
                    viewModel = viewModel,
                    onNavigateBackToCamera = {
                        navController.navigate(AppDestination.CAMERA.route) {
                            popUpTo(AppDestination.CAMERA.route) { inclusive = true }
                        }
                    },
                    onNavigateToPoses = { navController.navigate(AppDestination.POSES.route) }
                )
            }

            composable(AppDestination.SETTINGS.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PoseXBottomNavigation(
    currentRoute: String?,
    onNavigate: (AppDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("bottom_nav_bar")
    ) {
        NavigationBar(
            containerColor = Color(0xF2141121),
            tonalElevation = 6.dp,
            modifier = Modifier
                .height(68.dp)
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, PoseXGlassBorder, RoundedCornerShape(26.dp))
        ) {
            AppDestination.bottomNavDestinations.forEach { destination ->
                val isSelected = currentRoute == destination.route
                val isCamera = destination == AppDestination.CAMERA

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(destination) },
                    modifier = Modifier.testTag("nav_tab_${destination.route}"),
                    icon = {
                        if (isCamera) {
                            // Elevated glowing center camera button
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(PoseXElectricViolet)
                                    .border(1.5.dp, PoseXLavender, CircleShape)
                            ) {
                                Icon(
                                    imageVector = destination.selectedIcon(),
                                    contentDescription = destination.title,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon() else destination.unselectedIcon(),
                                contentDescription = destination.title,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        if (!isCamera) {
                            Text(
                                text = destination.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PoseXLavender,
                        selectedTextColor = Color.White,
                        unselectedIconColor = PoseXTextMuted,
                        unselectedTextColor = PoseXTextMuted,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
