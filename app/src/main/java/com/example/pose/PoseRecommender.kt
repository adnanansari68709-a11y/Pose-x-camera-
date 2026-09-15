package com.example.pose

import com.example.model.PoseTemplate

interface PoseRecommender {
    suspend fun getRecommendations(
        allPoses: List<PoseTemplate>,
        recentPoseIds: List<String>,
        favoritePoseIds: Set<String>,
        currentCategory: String,
        isFrontCamera: Boolean,
        isFullBodyOnly: Boolean
    ): List<PoseTemplate>
}

/**
 * Robust on-device recommendation engine that runs offline without API keys.
 */
class LocalPoseRecommender : PoseRecommender {

    override suspend fun getRecommendations(
        allPoses: List<PoseTemplate>,
        recentPoseIds: List<String>,
        favoritePoseIds: Set<String>,
        currentCategory: String,
        isFrontCamera: Boolean,
        isFullBodyOnly: Boolean
    ): List<PoseTemplate> {
        if (allPoses.isEmpty()) return emptyList()

        // Score each pose based on current context
        val scoredPoses = allPoses.map { pose ->
            var score = 100

            // 1. Camera orientation / lens facing
            if (isFrontCamera) {
                // Selfie / mirror favors upper body poses
                if (!pose.isFullBody || pose.category == "Mirror" || pose.category == "Upper Body") {
                    score += 40
                }
            } else {
                // Rear camera favors full body, travel, street
                if (pose.isFullBody) {
                    score += 35
                }
            }

            // 2. Full-body constraint
            if (isFullBodyOnly && !pose.isFullBody) {
                score -= 60
            }

            // 3. Category matching
            if (currentCategory != "All") {
                if (pose.category.equals(currentCategory, ignoreCase = true) ||
                    pose.tags.any { it.equals(currentCategory, ignoreCase = true) }
                ) {
                    score += 50
                }
            }

            // 4. Favorites affinity (boost poses sharing tags with user's favorites)
            if (favoritePoseIds.contains(pose.id)) {
                score += 20
            }

            // 5. Novelty (demote recently used slightly so recommendations rotate)
            val recentIndex = recentPoseIds.indexOf(pose.id)
            if (recentIndex >= 0) {
                score -= (30 - recentIndex * 5).coerceAtLeast(10)
            } else {
                score += 15 // Novel pose bonus
            }

            Pair(pose, score)
        }

        return scoredPoses
            .sortedByDescending { it.second }
            .map { it.first }
            .take(6)
    }
}
