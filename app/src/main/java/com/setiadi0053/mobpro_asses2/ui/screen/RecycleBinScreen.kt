package com.setiadi0053.mobpro_asses2.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setiadi0053.mobpro_asses2.data.entity.Achievement
import com.setiadi0053.mobpro_asses2.ui.AchievementViewModel
import com.setiadi0053.mobpro_asses2.ui.theme.MobProAsses2Theme

@Composable
fun RecycleBinScreen(
    viewModel: AchievementViewModel,
    onNavigateBack: () -> Unit
) {
    val deletedAchievements by viewModel.deletedAchievements.collectAsState()

    RecycleBinContent(
        deletedAchievements = deletedAchievements,
        onNavigateBack = onNavigateBack,
        onRestore = { viewModel.restore(it) },
        onDeleteForever = { viewModel.deletePermanently(it) },
        onEmptyBin = { viewModel.emptyRecycleBin() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinContent(
    deletedAchievements: List<Achievement>,
    onNavigateBack: () -> Unit,
    onRestore: (Achievement) -> Unit,
    onDeleteForever: (Achievement) -> Unit,
    onEmptyBin: () -> Unit
) {
    var showDeleteForeverDialog by remember { mutableStateOf<Achievement?>(null) }
    var showEmptyBinDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recycle Bin") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (deletedAchievements.isNotEmpty()) {
                        IconButton(onClick = { showEmptyBinDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Empty Bin")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (deletedAchievements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Recycle Bin is empty.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(deletedAchievements) { achievement ->
                    DeletedAchievementItem(
                        achievement = achievement,
                        onRestore = { onRestore(achievement) },
                        onDeleteForever = { showDeleteForeverDialog = achievement }
                    )
                }
            }
        }
    }

    if (showDeleteForeverDialog != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Delete Permanently?") },
            text = { Text("This action cannot be undone. The achievement log will be gone forever.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteForeverDialog?.let { onDeleteForever(it) }
                    showDeleteForeverDialog = null
                }) {
                    Text("Delete Forever", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEmptyBinDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Empty Recycle Bin?") },
            text = { Text("All items in the Recycle Bin will be permanently deleted. This action is irreversible.") },
            confirmButton = {
                TextButton(onClick = {
                    onEmptyBin()
                }) {
                    Text("Empty Bin", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DeletedAchievementItem(
    achievement: Achievement,
    onRestore: () -> Unit,
    onDeleteForever: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = achievement.name, fontWeight = FontWeight.Bold)
                Text(text = "Soft-deleted achievement", style = MaterialTheme.typography.bodySmall)
            }
            
            IconButton(onClick = onRestore) {
                Icon(Icons.Default.Refresh, contentDescription = "Restore", tint = Color.Blue)
            }

            IconButton(onClick = onDeleteForever) {
                Icon(Icons.Default.DeleteForever, contentDescription = "Delete Forever", tint = Color.Red)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecycleBinPreview() {
    MobProAsses2Theme {
        RecycleBinContent(
            deletedAchievements = listOf(
                Achievement(1, 1, "Forgotten Legend", "An old achievement", 0, isDeleted = true),
                Achievement(2, 2, "Mistake Log", "I didn't earn this", 0, isDeleted = true)
            ),
            onNavigateBack = {},
            onRestore = {},
            onDeleteForever = {},
            onEmptyBin = {}
        )
    }
}
