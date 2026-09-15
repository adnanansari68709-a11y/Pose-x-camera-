package com.example

import com.example.model.PoseLandmarkPoint
import com.example.pose.LocalPoseRecommender
import com.example.pose.PoseLandmarkConstants
import com.example.pose.PoseLibraryData
import com.example.pose.PoseMatcher
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun poseLibrary_containsRequiredCategoriesAndPoses() {
        val poses = PoseLibraryData.getStarterPoses()
        assertTrue("Starter poses should not be empty", poses.size >= 10)

        val categories = PoseLibraryData.CATEGORIES
        assertTrue(categories.contains("Standing"))
        assertTrue(categories.contains("Sitting"))
        assertTrue(categories.contains("Mirror"))
        assertTrue(categories.contains("Street"))
        assertTrue(categories.contains("Fashion"))
        assertTrue(categories.contains("Casual"))
        assertTrue(categories.contains("Travel"))
        assertTrue(categories.contains("Indoor"))
        assertTrue(categories.contains("Outdoor"))
        assertTrue(categories.contains("Full Body"))
        assertTrue(categories.contains("Upper Body"))
    }

    @Test
    fun poseMatcher_identicalPose_yieldsHighMatchScore() {
        val poses = PoseLibraryData.getStarterPoses()
        val template = poses.first()

        val matcher = PoseMatcher()
        val result = matcher.match(
            detected = template.landmarks,
            reference = template.landmarks,
            isFullBodyRequired = template.isFullBody
        )

        assertTrue("Identical pose match score should be high (>= 75)", result.overallScore >= 75)
        assertTrue("isMatched should be true", result.isMatched)
    }

    @Test
    fun poseMatcher_emptyDetection_handlesGracefullyWithoutCrash() {
        val poses = PoseLibraryData.getStarterPoses()
        val template = poses.first()

        val matcher = PoseMatcher()
        val result = matcher.match(
            detected = emptyMap(),
            reference = template.landmarks,
            isFullBodyRequired = template.isFullBody
        )

        assertEquals(0, result.overallScore)
        assertFalse(result.isMatched)
        assertNotNull(result.primarySuggestion)
    }

    @Test
    fun poseRecommender_suggestsPosesAppropriateForCameraOrientation() = runBlocking {
        val recommender = LocalPoseRecommender()
        val poses = PoseLibraryData.getStarterPoses()

        val frontCameraRecommendations = recommender.getRecommendations(
            allPoses = poses,
            recentPoseIds = emptyList(),
            favoritePoseIds = emptySet(),
            currentCategory = "All",
            isFrontCamera = true,
            isFullBodyOnly = false
        )

        assertTrue(frontCameraRecommendations.isNotEmpty())
    }

    @Test
    fun navigation_bottomNavItems_areNonNullAndValid() {
        val items = com.example.ui.navigation.AppDestination.bottomNavDestinations
        assertEquals(5, items.size)
        items.forEach { destination ->
            assertNotNull("Bottom nav destination must not be null", destination)
            assertNotNull("Destination route must not be null", destination.route)
            assertTrue("Destination route must not be empty", destination.route.isNotEmpty())
            assertNotNull("Destination title must not be null", destination.title)
        }
    }
}
