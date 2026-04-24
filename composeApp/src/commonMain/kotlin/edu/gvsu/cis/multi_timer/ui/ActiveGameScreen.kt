package edu.gvsu.cis.multi_timer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel
import edu.gvsu.cis.multi_timer.ui.playsetScreens.*

@Composable
fun ActiveGameScreen(viewModel: ActiveGameViewModel, onExitGame: () -> Unit) {
    val activeGameState by viewModel.gameState.collectAsState()
    when (activeGameState?.currentPlayersState?.size ?: 5) {
        1 -> OnePlayerScreen(viewModel = viewModel, activeGameState = activeGameState!!)
        2 -> TwoPlayerScreen(viewModel = viewModel, activeGameState = activeGameState!!)
        3 -> ThreePlayerScreen(viewModel = viewModel, activeGameState = activeGameState!!)
        4 -> FourPlayerScreen(viewModel = viewModel, activeGameState = activeGameState!!)
        else -> LoadingErrorScreen(onExitGame)
    }
}