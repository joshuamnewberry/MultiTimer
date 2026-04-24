package edu.gvsu.cis.multi_timer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.gvsu.cis.multi_timer.ui.playsetScreens.*
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun ActiveGameScreen(viewModel: ActiveGameViewModel, onExitGame: () -> Unit) {
    val activeGameState by viewModel.gameState.collectAsState()
    when (val playerCount = activeGameState?.currentPlayersState?.size ?: 5) {

        // 1-Player handles its own button placement at the bottom
        1 -> OnePlayerScreen(viewModel = viewModel, activeGameState = activeGameState!!)

        2, 3, 4 -> {
            Box(modifier = Modifier.fillMaxSize()) {

                when (playerCount) {
                    2 -> TwoPlayerScreen(viewModel = viewModel, activeGameState = activeGameState!!)
                    3 -> ThreePlayerScreen(viewModel = viewModel, activeGameState = activeGameState!!)
                    4 -> FourPlayerScreen(viewModel = viewModel, activeGameState = activeGameState!!)
                }
                // Overlay the shared Play/Pause button in the exact center
                PlayPauseButton(
                    isGamePaused = activeGameState!!.isGamePaused,
                    onClick = { viewModel.toggleGlobalPlayPause() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        else -> LoadingErrorScreen(onExitGame)
    }
}