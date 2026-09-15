package com.example

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.pose.PoseLibraryData
import com.example.ui.components.PoseCard
import com.example.ui.theme.PosexTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun poseCard_screenshot() {
        val samplePose = PoseLibraryData.getStarterPoses().first()

        composeTestRule.setContent {
            PosexTheme(darkTheme = true) {
                PoseCard(
                    modifier = Modifier.padding(16.dp),
                    pose = samplePose,
                    onPoseClick = {},
                    onFavoriteToggle = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/pose_card.png")
    }
}
