package edu.gvsu.cis.multi_timer.ui.playsetScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.data.ActiveGameState
import edu.gvsu.cis.multi_timer.data.Player
import edu.gvsu.cis.multi_timer.ui.PlayPauseButton
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun OnePlayerScreen(
    viewModel: ActiveGameViewModel,
    activeGameState: ActiveGameState,
    activePlayers: Map<Int, Player>,
    onExitGame: () -> Unit,
    onRequestRestart: () -> Unit
) {
    val playset = activeGameState.currentPlayset
    val config = playset.autoAdvance
    val incrementMs = playset.incrementSeconds * 1000L

    Box(modifier = Modifier.fillMaxSize()) {
        val p0 = activeGameState.currentPlayersState[0]

        PlayerCounterScreen(
            playerState = p0,
            playerProfile = activePlayers[p0.playerProfileId],
            isGamePaused = activeGameState.isGamePaused,
            isAutoAdvanceEnabled = config.enabled,
            onClick = { viewModel.handleInteraction(0, config, incrementMs) },
            onLifeChange = { amount -> viewModel.updateLife(0, amount) },
            modifier = Modifier.fillMaxSize(),
            rotation = 0f
        )

        if (activeGameState.isGamePaused) {
            // Home Button
            FloatingActionButton(
                onClick = onExitGame,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .offset(x = (-96).dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home")
            }

            // Restart Button
            FloatingActionButton(
                onClick = onRequestRestart,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .offset(x = 96.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Restart Game")
            }
        }

        // Play/Pause Button
        PlayPauseButton(
            isGamePaused = activeGameState.isGamePaused,
            onClick = { viewModel.toggleGlobalPlayPause() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )
    }
}