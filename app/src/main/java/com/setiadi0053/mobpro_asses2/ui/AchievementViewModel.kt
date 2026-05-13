package com.setiadi0053.mobpro_asses2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.setiadi0053.mobpro_asses2.data.AppDatabase
import com.setiadi0053.mobpro_asses2.data.PreferenceManager
import com.setiadi0053.mobpro_asses2.data.entity.Achievement
import com.setiadi0053.mobpro_asses2.data.entity.Tf2Class
import com.setiadi0053.mobpro_asses2.data.repository.AchievementRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AchievementViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AchievementRepository
    private val preferenceManager = PreferenceManager(application)

    val teamTheme: StateFlow<String> = preferenceManager.teamTheme.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "RED"
    )
    
    val sortByDate: StateFlow<Boolean> = preferenceManager.sortByDate.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites = _showOnlyFavorites.asStateFlow()

    val allClasses: StateFlow<List<Tf2Class>>
    val allAchievements: StateFlow<List<Achievement>>
    val deletedAchievements: StateFlow<List<Achievement>>
    val achievementCounts: StateFlow<Map<Int, Int>>

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

        achievementCounts = allAchievements.map { list ->
            list.groupBy { it.classId }.mapValues { it.value.size }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

        viewModelScope.launch {
            val currentClasses = repository.allClasses.first()
            if (currentClasses.isEmpty()) {
                seedInitialData()
            }
        }
    }

    private suspend fun seedInitialData() {
        val theme = teamTheme.value
        val color = if (theme == "RED") "#BD3B3B" else "#5885A2"
        val classes = listOf(
            Tf2Class(1, "Scout", color),
            Tf2Class(2, "Soldier", color),
            Tf2Class(3, "Pyro", color),
            Tf2Class(4, "Demoman", color),
            Tf2Class(5, "Heavy", color),
            Tf2Class(6, "Engineer", color),
            Tf2Class(7, "Medic", color),
            Tf2Class(8, "Sniper", color),
            Tf2Class(9, "Spy", color)
        )
        repository.insertClasses(classes)

        val initialAchievements = listOf(
            Achievement(name = "First Blood", description = "Get the first kill in a round.", classId = 1, dateObtained = System.currentTimeMillis()),
            Achievement(name = "Grey Matter", description = "Get 25 headshots as a Sniper.", classId = 8, dateObtained = System.currentTimeMillis() - 86400000),
            Achievement(name = "Specialist", description = "Accumulate 10,000 heal points in a single life.", classId = 7, dateObtained = System.currentTimeMillis() - 172800000)
        )
        initialAchievements.forEach { repository.insertAchievement(it) }
    }

    fun getAchievementsByClass(classId: Int): Flow<List<Achievement>> {
        return repository.getAchievementsByClass(classId)
            .combine(sortByDate) { list, sort ->
                if (sort) list.sortedByDescending { it.dateObtained } else list.sortedBy { it.dateObtained }
            }
            .combine(searchQuery) { list, query ->
                if (query.isBlank()) list else list.filter { 
                    it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) 
                }
            }
            .combine(showOnlyFavorites) { list, favoritesOnly ->
                if (favoritesOnly) list.filter { it.isFavorite } else list
            }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun toggleFavoriteFilter() { _showOnlyFavorites.value = !_showOnlyFavorites.value }

    fun insert(achievement: Achievement) = viewModelScope.launch { repository.insertAchievement(achievement) }
    fun update(achievement: Achievement) = viewModelScope.launch { repository.updateAchievement(achievement) }
    fun moveToRecycleBin(achievement: Achievement) = viewModelScope.launch { repository.moveToRecycleBin(achievement) }
    fun restore(achievement: Achievement) = viewModelScope.launch { repository.restoreFromRecycleBin(achievement) }
    fun deletePermanently(achievement: Achievement) = viewModelScope.launch { repository.deleteAchievement(achievement) }

    fun emptyRecycleBin() = viewModelScope.launch {
        deletedAchievements.value.forEach { repository.deleteAchievement(it) }
    }
    
    suspend fun getAchievementById(id: Int): Achievement? = repository.getAchievementById(id)

    fun setTeamTheme(theme: String) = viewModelScope.launch {
        preferenceManager.saveTeamTheme(theme)
        updateClassColors(theme)
    }

    fun setSortOrder(recentFirst: Boolean) = viewModelScope.launch {
        preferenceManager.saveSortOrder(recentFirst)
    }

    private suspend fun updateClassColors(theme: String) {
        val color = if (theme == "RED") "#BD3B3B" else "#5885A2"
        val currentClasses = repository.allClasses.first()
        if (currentClasses.isNotEmpty()) {
            val updatedClasses = currentClasses.map { it.copy(teamColor = color) }
            repository.insertClasses(updatedClasses)
        }
    }
}
