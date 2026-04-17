package edu.gvsu.cis.multi_timer.data

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.gvsu.cis.multi_timer.ui.EditPlaysetsScreen
import edu.gvsu.cis.multi_timer.ui.HomeScreen
import edu.gvsu.cis.multi_timer.ui.SettingsScreen
import edu.gvsu.cis.multi_timer.ui.playsetScreens.FourPlayerScreen
import edu.gvsu.cis.multi_timer.ui.playsetScreens.OnePlayerScreen
import edu.gvsu.cis.multi_timer.ui.playsetScreens.ThreePlayerScreen
import edu.gvsu.cis.multi_timer.ui.playsetScreens.TwoPlayerScreen
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel
import edu.gvsu.cis.multi_timer.viewModels.EditPlaysetsViewModel
import edu.gvsu.cis.multi_timer.viewModels.HomeViewModel
import edu.gvsu.cis.multi_timer.viewModels.SettingsViewModel

@Composable
fun App(dao: AppDAO) {
    MaterialTheme {
        val navController = rememberNavController()
        val homeVM = HomeViewModel(dao = dao)
        val settingsVM = SettingsViewModel(dao = dao)
        val editorVM = EditPlaysetsViewModel(dao = dao)
        val gameVM = ActiveGameViewModel(dao = dao)

        NavHost(navController = navController, startDestination = "home") {
            composable("main") {
                HomeScreen(
                    viewModel = homeVM,
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToEditPlaysets = { navController.navigate("editor") },
                    onNavigateToPlay = { navController.navigate(homeVM.currentPlayset.value.playerCount.toString())}
                )
            }
            composable("settings") {
                SettingsScreen(viewModel = settingsVM, onBack = { navController.popBackStack()})
            }
            composable("editor") {
                EditPlaysetsScreen(viewModel = editorVM, onBack = { navController.popBackStack() })
            }
            composable("1") {
                OnePlayerScreen(viewModel = gameVM, onBack = { navController.popBackStack() })
            }
            composable("2") {
                TwoPlayerScreen(viewModel = gameVM, onBack = { navController.popBackStack() })
            }
            composable("3") {
                ThreePlayerScreen(viewModel = gameVM, onBack = { navController.popBackStack() })
            }
            composable("4") {
                FourPlayerScreen(viewModel = gameVM, onBack = { navController.popBackStack() })
            }
        }
    }
}