package com.setiadi0053.mobpro_asses2.data.repository

import com.setiadi0053.mobpro_asses2.data.dao.AchievementDao
import com.setiadi0053.mobpro_asses2.data.entity.Achievement
import com.setiadi0053.mobpro_asses2.data.entity.Tf2Class
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val achievementDao: AchievementDao) {
    val allAchievements: Flow<List<Achievement>> = achievementDao.getAllAchievements()
    val deletedAchievements: Flow<List<Achievement>> = achievementDao.getDeletedAchievements()
    val allClasses: Flow<List<Tf2Class>> = achievementDao.getAllClasses()

    fun getAchievementsByClass(classId: Int): Flow<List<Achievement>> {
        return achievementDao.getAchievementsByClass(classId)
    }

    suspend fun insertAchievement(achievement: Achievement) {
        achievementDao.insertAchievement(achievement)
    }

    suspend fun updateAchievement(achievement: Achievement) {
        achievementDao.updateAchievement(achievement)
    }

    suspend fun deleteAchievement(achievement: Achievement) {
        achievementDao.deleteAchievement(achievement)
    }

    suspend fun insertClasses(classes: List<Tf2Class>) {
        achievementDao.insertClasses(classes)
    }

    suspend fun getAchievementById(id: Int): Achievement? {
        return achievementDao.getAchievementById(id)
    }

    suspend fun moveToRecycleBin(achievement: Achievement) {
        achievementDao.updateAchievement(achievement.copy(isDeleted = true))
    }

    suspend fun restoreFromRecycleBin(achievement: Achievement) {
        achievementDao.updateAchievement(achievement.copy(isDeleted = false))
    }
}
