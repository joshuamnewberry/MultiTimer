package edu.gvsu.cis.multi_timer.ui.playsetScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.data.ActiveGameState
import edu.gvsu.cis.multi_timer.ui.PlayPauseButton
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun OnePlayerScreen(viewModel: ActiveGameViewModel, activeGameState: ActiveGameState) {
    val playset = activeGameState.currentPlayset
    val config = playset.autoAdvance
    val incrementMs = playset.incrementSeconds * 1000L

    Box(modifier = Modifier.fillMaxSize()) {
        PlayerCounterScreen(
            playerState = activeGameState.currentPlayersState[0],
            isGamePaused = activeGameState.isGamePaused,
            onClick = { viewModel.handleInteraction(0, config, incrementMs) },
            onLifeChange = { amount -> viewModel.updateLife(0, amount) },
            modifier = Modifier.fillMaxSize(),
            rotation = 0f
        )

        // The Bottom-Center Play/Pause Overlay
        PlayPauseButton(
            isGamePaused = activeGameState.isGamePaused,
            onClick = { viewModel.toggleGlobalPlayPause() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )
    }
}