package com.setiadi0053.mobpro_asses2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.setiadi0053.mobpro_asses2.data.entity.Achievement
import com.setiadi0053.mobpro_asses2.data.entity.Tf2Class
import com.setiadi0053.mobpro_asses2.ui.AchievementViewModel
import com.setiadi0053.mobpro_asses2.ui.theme.MobProAsses2Theme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AchievementListScreen(
    viewModel: AchievementViewModel,
    classId: Int,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onRecycleBinClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val achievements by viewModel.getAchievementsByClass(classId).collectAsState(initial = emptyList())
    val classes by viewModel.allClasses.collectAsState()
    val tf2Class = classes.find { it.id == classId }

    AchievementListContent(
        tf2Class = tf2Class,
        achievements = achievements,
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onRecycleBinClick = onRecycleBinClick,
        onBackClick = onBackClick,
        onToggleFavorite = { viewModel.update(it.copy(isFavorite = !it.isFavorite)) },
        onMoveToRecycleBin = { viewModel.moveToRecycleBin(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementListContent(
    tf2Class: Tf2Class?,
    achievements: List<Achievement>,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onRecycleBinClick: () -> Unit,
    onBackClick: () -> Unit,
    onToggleFavorite: (Achievement) -> Unit,
    onMoveToRecycleBin: (Achievement) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<Achievement?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${tf2Class?.name ?: "Class"} Achievements") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRecycleBinClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Recycle Bin")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Achievement")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (achievements.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No achievements logged for ${tf2Class?.name ?: "this class"}.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(achievements) { achievement ->
                        AchievementItem(
                            achievement = achievement,
                            tf2Class = tf2Class,
                            onEdit = { onEditClick(achievement.id) },
                            onDelete = { showDeleteDialog = achievement },
                            onToggleFavorite = { onToggleFavorite(achievement) }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Move this achievement to the Recycle Bin?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog?.let { onMoveToRecycleBin(it) }
                    showDeleteDialog = null
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AchievementItem(
    achievement: Achievement,
    tf2Class: Tf2Class?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val colorString = tf2Class?.teamColor ?: "#CCCCCC"
    val color = remember(colorString) {
        try {
            Color(android.graphics.Color.parseColor(colorString))
        } catch (e: Exception) {
            Color.Gray
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tf2Class?.name?.take(1) ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = achievement.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(achievement.dateObtained)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (achievement.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (achievement.isFavorite) Color.Red else Color.Gray
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AchievementListPreview() {
    MobProAsses2Theme {
        AchievementListContent(
            tf2Class = Tf2Class(1, "Scout", "#BD3B3B"),
            achievements = listOf(
                Achievement(1, 1, "First Blood", "Get the first kill in a round", System.currentTimeMillis()),
                Achievement(2, 1, "Triple Play", "Capture three points in a single round", System.currentTimeMillis(), isFavorite = true)
            ),
            onAddClick = {},
            onEditClick = {},
            onRecycleBinClick = {},
            onBackClick = {},
            onToggleFavorite = {},
            onMoveToRecycleBin = {}
        )
    }
}
