package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface PoseDao {

    @Query("SELECT * FROM favorite_poses ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoritePoseEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_poses WHERE poseId = :poseId)")
    fun isFavorite(poseId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoritePoseEntity)

    @Query("DELETE FROM favorite_poses WHERE poseId = :poseId")
    suspend fun deleteFavorite(poseId: String)

    @Query("SELECT * FROM recent_poses ORDER BY lastUsedAt DESC LIMIT 10")
    fun getRecentPoses(): Flow<List<RecentPoseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecentPose(recent: RecentPoseEntity)

    @Query("SELECT * FROM capture_history ORDER BY timestamp DESC")
    fun getCaptureHistory(): Flow<List<CaptureHistoryEntity>>

    @Query("SELECT COUNT(*) FROM capture_history")
    fun getTotalCapturesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapture(capture: CaptureHistoryEntity)
}

@Database(
    entities = [
        FavoritePoseEntity::class,
        RecentPoseEntity::class,
        CaptureHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PoseDatabase : RoomDatabase() {
    abstract fun poseDao(): PoseDao

    companion object {
        @Volatile
        private var INSTANCE: PoseDatabase? = null

        fun getDatabase(context: Context): PoseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PoseDatabase::class.java,
                    "posex_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
