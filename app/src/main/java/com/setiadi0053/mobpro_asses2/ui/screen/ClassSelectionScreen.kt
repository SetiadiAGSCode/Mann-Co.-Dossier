package com.setiadi0053.mobpro_asses2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.setiadi0053.mobpro_asses2.data.entity.Tf2Class
import com.setiadi0053.mobpro_asses2.ui.AchievementViewModel
import com.setiadi0053.mobpro_asses2.ui.theme.MobProAsses2Theme

@Composable
fun ClassSelectionScreen(
    viewModel: AchievementViewModel,
    onClassClick: (Int) -> Unit,
    onSettingsClick: () -> Unit
) {
    val classes by viewModel.allClasses.collectAsState()
    val counts by viewModel.achievementCounts.collectAsState()
    
    ClassSelectionContent(
        classes = classes,
        counts = counts,
        onClassClick = onClassClick,
        onSettingsClick = onSettingsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassSelectionContent(
    classes: List<Tf2Class>,
    counts: Map<Int, Int>,
    onClassClick: (Int) -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TF2 Achievements") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(
                text = "Track your achievements for each class:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(classes) { tf2Class ->
                    val count = counts[tf2Class.id] ?: 0
                    ClassCard(
                        tf2Class = tf2Class,
                        achievementCount = count,
                        onClick = { onClassClick(tf2Class.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClassCard(tf2Class: Tf2Class, achievementCount: Int, onClick: () -> Unit) {
    val color = try {
        Color(tf2Class.teamColor.toColorInt())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tf2Class.name.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tf2Class.name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$achievementCount Logged",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClassSelectionPreview() {
    MobProAsses2Theme {
        ClassSelectionContent(
            classes = listOf(
                Tf2Class(1, "Scout", "#BD3B3B"),
                Tf2Class(2, "Soldier", "#BD3B3B")
            ),
            counts = mapOf(1 to 5, 2 to 0),
            onClassClick = {},
            onSettingsClick = {}
        )
    }
}
