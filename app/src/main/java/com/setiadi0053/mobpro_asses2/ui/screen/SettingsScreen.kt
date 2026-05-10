package com.setiadi0053.mobpro_asses2.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setiadi0053.mobpro_asses2.ui.AchievementViewModel
import com.setiadi0053.mobpro_asses2.ui.theme.MobProAsses2Theme

@Composable
fun SettingsScreen(
    viewModel: AchievementViewModel,
    onNavigateBack: () -> Unit
) {
    val teamTheme by viewModel.teamTheme.collectAsState()
    val sortByDate by viewModel.sortByDate.collectAsState()

    SettingsContent(
        teamTheme = teamTheme,
        sortByDate = sortByDate,
        onTeamThemeChange = { viewModel.setTeamTheme(it) },
        onSortOrderChange = { viewModel.setSortOrder(it) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    teamTheme: String,
    sortByDate: Boolean,
    onTeamThemeChange: (String) -> Unit,
    onSortOrderChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column {
                Text(text = "Team Theme", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Choose your side (Affects class colors)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = teamTheme == "RED",
                            onClick = { onTeamThemeChange("RED") }
                        )
                        Text("RED")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = teamTheme == "BLU",
                            onClick = { onTeamThemeChange("BLU") }
                        )
                        Text("BLU")
                    }
                }
            }

            Divider()

            Column {
                Text(text = "Sort Order", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Show most recent first")
                    Switch(
                        checked = sortByDate,
                        onCheckedChange = onSortOrderChange
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MobProAsses2Theme {
        SettingsContent(
            teamTheme = "RED",
            sortByDate = true,
            onTeamThemeChange = {},
            onSortOrderChange = {},
            onNavigateBack = {}
        )
    }
}
