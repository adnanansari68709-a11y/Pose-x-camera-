package com.example.data

import com.example.model.PoseLandmarkPoint
import com.example.model.PoseTemplate
import com.example.pose.LocalPoseRecommender
import com.example.pose.PoseLibraryData
import com.example.pose.PoseRecommender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class PoseRepository(
    private val poseDao: PoseDao,
    private val recommender: PoseRecommender = LocalPoseRecommender()
) {

    // In-memory custom poses extracted by user from gallery photos
    private val customPoses = MutableStateFlow<List<PoseTemplate>>(emptyList())

    /**
     * All poses (starter library + custom extracted poses), reactively merged with favorite status.
     */
    fun getAllPoses(): Flow<List<PoseTemplate>> {
        val basePoses = PoseLibraryData.getStarterPoses()
        return combine(customPoses, poseDao.getAllFavorites()) { customs, favorites ->
            val favIds = favorites.map { it.poseId }.toSet()
            (customs + basePoses).map { pose ->
                pose.copy(isFavorite = favIds.contains(pose.id))
            }
        }
    }

    /**
     * Get a specific pose by ID.
     */
    fun getPoseById(id: String): Flow<PoseTemplate?> {
        return combine(getAllPoses()) { all ->
            all[0].find { it.id == id }
        }
    }

    /**
     * Get favorite poses.
     */
    fun getFavoritePoses(): Flow<List<PoseTemplate>> {
        return combine(getAllPoses()) { all ->
            all[0].filter { it.isFavorite }
        }
    }

    /**
     * Get recently used poses.
     */
    fun getRecentPoses(): Flow<List<PoseTemplate>> {
        return combine(getAllPoses(), poseDao.getRecentPoses()) { all, recents ->
            val recentMap = recents.associateBy({ it.poseId }, { it.lastUsedAt })
            all.filter { recentMap.containsKey(it.id) }
                .sortedByDescending { recentMap[it.id] ?: 0L }
        }
    }

    /**
     * Toggle favorite state.
     */
    suspend fun toggleFavorite(poseId: String) {
        val isFav = poseDao.isFavorite(poseId).first()
        if (isFav) {
            poseDao.deleteFavorite(poseId)
        } else {
            poseDao.insertFavorite(FavoritePoseEntity(poseId = poseId))
        }
    }

    /**
     * Mark a pose as recently used when selected/captured.
     */
    suspend fun markPoseUsed(poseId: String) {
        poseDao.upsertRecentPose(RecentPoseEntity(poseId = poseId, lastUsedAt = System.currentTimeMillis()))
    }

    /**
     * Add a custom pose extracted from a user's image.
     */
    fun addCustomPose(
        title: String,
        landmarks: Map<Int, PoseLandmarkPoint>,
        isFullBody: Boolean = true
    ): PoseTemplate {
        val newPose = PoseTemplate(
            id = "custom_pose_${System.currentTimeMillis()}",
            title = title.ifBlank { "Custom Pose" },
            category = "Custom",
            difficulty = "Custom",
            description = "Custom extracted pose from your gallery photo.",
            instructions = listOf(
                "Match the extracted posture lines.",
                "Ensure your body aligns with the cyan keypoints.",
                "Hold position until score reaches 75%+."
            ),
            landmarks = landmarks,
            tags = listOf("Custom", if (isFullBody) "Full Body" else "Upper Body"),
            isFullBody = isFullBody,
            isCustom = true
        )
        customPoses.value = listOf(newPose) + customPoses.value
        return newPose
    }

    /**
     * Save a photo capture record to Room database.
     */
    suspend fun saveCapture(uriString: String, poseTitle: String, matchScore: Int) {
        poseDao.insertCapture(
            CaptureHistoryEntity(
                uriString = uriString,
                poseTitle = poseTitle,
                matchScore = matchScore,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun getTotalCapturesCount(): Flow<Int> = poseDao.getTotalCapturesCount()

    fun getCaptureHistory(): Flow<List<CaptureHistoryEntity>> = poseDao.getCaptureHistory()

    /**
     * AI pose recommendations.
     */
    suspend fun getRecommendations(
        currentCategory: String,
        isFrontCamera: Boolean,
        isFullBodyOnly: Boolean
    ): List<PoseTemplate> {
        val all = getAllPoses().first()
        val recents = poseDao.getRecentPoses().first().map { it.poseId }
        val favorites = poseDao.getAllFavorites().first().map { it.poseId }.toSet()
        return recommender.getRecommendations(
            allPoses = all,
            recentPoseIds = recents,
            favoritePoseIds = favorites,
            currentCategory = currentCategory,
            isFrontCamera = isFrontCamera,
            isFullBodyOnly = isFullBodyOnly
        )
    }
}
