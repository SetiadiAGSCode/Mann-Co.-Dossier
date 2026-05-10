package com.setiadi0053.mobpro_asses2.data.dao

import androidx.room.*
import com.setiadi0053.mobpro_asses2.data.entity.Achievement
import com.setiadi0053.mobpro_asses2.data.entity.Tf2Class
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Delete
    suspend fun deleteAchievement(achievement: Achievement)

    @Query("SELECT * FROM achievements WHERE isDeleted = 0 AND classId = :classId")
    fun getAchievementsByClass(classId: Int): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isDeleted = 0")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isDeleted = 1")
    fun getDeletedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM tf2_classes")
    fun getAllClasses(): Flow<List<Tf2Class>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<Tf2Class>)

    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getAchievementById(id: Int): Achievement?
}
