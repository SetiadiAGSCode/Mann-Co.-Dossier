package com.setiadi0053.mobpro_asses2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.setiadi0053.mobpro_asses2.ui.AchievementViewModel
import com.setiadi0053.mobpro_asses2.ui.screen.*
import com.setiadi0053.mobpro_asses2.ui.theme.MobProAsses2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AchievementViewModel = viewModel()
            val teamTheme by viewModel.teamTheme.collectAsState()

            MobProAsses2Theme(teamTheme = teamTheme) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "class_selection") {
                    composable("class_selection") {
                        ClassSelectionScreen(
                            viewModel = viewModel,
                            onClassClick = { classId ->
                                navController.navigate("list/$classId")
                            },
                            onSettingsClick = {
                                navController.navigate("settings")
                            }
                        )
                    }
                    composable(
                        route = "list/{classId}",
                        arguments = listOf(navArgument("classId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val classId = backStackEntry.arguments?.getInt("classId") ?: 1
                        AchievementListScreen(
                            viewModel = viewModel,
                            classId = classId,
                            onAddClick = { navController.navigate("form?classId=$classId") },
                            onEditClick = { id -> navController.navigate("form?id=$id") },
                            onRecycleBinClick = { navController.navigate("recycle_bin") },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = "form?id={id}&classId={classId}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument("classId") {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id")
                        val classId = backStackEntry.arguments?.getInt("classId")
                        AchievementFormScreen(
                            viewModel = viewModel,
                            achievementId = if (id == null || id == -1) null else id,
                            initialClassId = if (classId == null || classId == -1) null else classId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("recycle_bin") {
                        RecycleBinScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
