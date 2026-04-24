package edu.gvsu.cis.multi_timer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.gvsu.cis.multi_timer.ui.*
import edu.gvsu.cis.multi_timer.viewModels.*
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            val viewModel = koinViewModel<HomeViewModel>()
            val sessionManager = koinInject<PlaysetSessionManager>()

            HomeScreen(
                viewModel = viewModel,
                onNavigateToActiveGame = { navController.navigate("activeGame") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToEditPlaysets = { playsetId ->
                    // Insert the ID then navigate
                    sessionManager.currentEditId = playsetId
                    navController.navigate("editPlaysets")
                }
            )
        }

        composable("settings") {
            val viewModel = koinViewModel<SettingsViewModel>()
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("editPlaysets") {
            val viewModel = koinViewModel<EditPlaysetsViewModel>()
            EditPlaysetsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("activeGame") {
            val viewModel = koinViewModel<ActiveGameViewModel>()
            ActiveGameScreen(
                viewModel = viewModel,
                onExitGame = { navController.popBackStack() }
            )
        }
    }
}