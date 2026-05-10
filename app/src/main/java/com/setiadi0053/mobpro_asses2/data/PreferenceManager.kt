package com.setiadi0053.mobpro_asses2.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class PreferenceManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val TEAM_THEME = stringPreferencesKey("team_theme")
        val SORT_BY_DATE = booleanPreferencesKey("sort_by_date")
    }

    val teamTheme: Flow<String> = dataStore.data.map { prefs ->
        prefs[TEAM_THEME] ?: "RED" // Default to RED team
    }

    val sortByDate: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SORT_BY_DATE] ?: true // Default to sorting by date
    }

    suspend fun saveTeamTheme(theme: String) {
        dataStore.edit { prefs ->
            prefs[TEAM_THEME] = theme
        }
    }

    suspend fun saveSortOrder(recentFirst: Boolean) {
        dataStore.edit { prefs ->
            prefs[SORT_BY_DATE] = recentFirst
        }
    }
}
