package com.setiadi0053.mobpro_asses2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.setiadi0053.mobpro_asses2.data.AppDatabase
import com.setiadi0053.mobpro_asses2.data.entity.Achievement
import com.setiadi0053.mobpro_asses2.data.entity.Tf2Class
import com.setiadi0053.mobpro_asses2.data.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AchievementViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AchievementRepository
    val allClasses: StateFlow<List<Tf2Class>>
    val allAchievements: StateFlow<List<Achievement>>
    val deletedAchievements: StateFlow<List<Achievement>>

    init {
        val dao = AppDatabase.getDatabase(application).achievementDao()
        repository = AchievementRepository(dao)
        
        allClasses = repository.allClasses.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allAchievements = repository.allAchievements.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        deletedAchievements = repository.deletedAchievements.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        seedClasses()
    }

    private fun seedClasses() {
        viewModelScope.launch {
            val classes = listOf(
                Tf2Class(1, "Scout", "#BD3B3B"),
                Tf2Class(2, "Soldier", "#BD3B3B"),
                Tf2Class(3, "Pyro", "#BD3B3B"),
                Tf2Class(4, "Demoman", "#BD3B3B"),
                Tf2Class(5, "Heavy", "#BD3B3B"),
                Tf2Class(6, "Engineer", "#BD3B3B"),
                Tf2Class(7, "Medic", "#BD3B3B"),
                Tf2Class(8, "Sniper", "#BD3B3B"),
                Tf2Class(9, "Spy", "#BD3B3B")
            )
            repository.insertClasses(classes)
        }
    }

    fun getAchievementsByClass(classId: Int): Flow<List<Achievement>> {
        return repository.getAchievementsByClass(classId)
    }

    fun insert(achievement: Achievement) = viewModelScope.launch {
        repository.insertAchievement(achievement)
    }

    fun update(achievement: Achievement) = viewModelScope.launch {
        repository.updateAchievement(achievement)
    }

    fun moveToRecycleBin(achievement: Achievement) = viewModelScope.launch {
        repository.moveToRecycleBin(achievement)
    }

    fun restore(achievement: Achievement) = viewModelScope.launch {
        repository.restoreFromRecycleBin(achievement)
    }

    fun deletePermanently(achievement: Achievement) = viewModelScope.launch {
        repository.deleteAchievement(achievement)
    }
    
    suspend fun getAchievementById(id: Int): Achievement? {
        return repository.getAchievementById(id)
    }
}
