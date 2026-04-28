package edu.gvsu.cis.multi_timer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.gvsu.cis.multi_timer.ui.*
import edu.gvsu.cis.multi_timer.viewModels.*
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor.KtorNetworkFetcherFactory

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .build()
    }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            val viewModel = koinViewModel<HomeViewModel>()
            val sessionManager = koinInject<sessionManager>()


            HomeScreen(
                viewModel = viewModel,
                onNavigateToActiveGame = { navController.navigate("activeGame") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToEditPlaysets = { playsetId ->
                    // Insert the ID then navigate
                    sessionManager.currentEditId = playsetId
                    navController.navigate("editPlaysets")
                },
                onNavigateToEditPlayers = { playersId ->
                    // Insert the ID then navigate
                    sessionManager.currentEditId = playersId
                    navController.navigate("editPlayers")
                },
            )
        }

        composable("settings") {
            val viewModel = koinViewModel<HomeViewModel>()
            SettingsScreen(
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

        composable("editPlaysets") {
            val viewModel = koinViewModel<EditPlaysetsViewModel>()
            EditPlaysetsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("editPlayers") {
            val viewModel = koinViewModel<EditPlayersViewModel>()
            EditPlayersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}