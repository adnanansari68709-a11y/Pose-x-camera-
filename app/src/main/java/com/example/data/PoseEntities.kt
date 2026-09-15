package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_poses")
data class FavoritePoseEntity(
    @PrimaryKey val poseId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_poses")
data class RecentPoseEntity(
    @PrimaryKey val poseId: String,
    val lastUsedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "capture_history")
data class CaptureHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uriString: String,
    val poseTitle: String,
    val matchScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)
