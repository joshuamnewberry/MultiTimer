package edu.gvsu.cis.multi_timer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.ui.playsetScreens.*
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun ActiveGameScreen(viewModel: ActiveGameViewModel, onExitGame: () -> Unit) {
    // Collect game data
    val activeGameState by viewModel.gameState.collectAsState()
    val activePlayers by viewModel.activePlayers.collectAsState()

    // UI variables
    var showRestartDialog by remember { mutableStateOf(false) }

    when (val playerCount = activeGameState?.currentPlayersState?.size ?: 5) {
        // One player screen handles the Play/Pause/Restart/Home UI on its own
        1 -> OnePlayerScreen(
            viewModel = viewModel,
            activeGameState = activeGameState!!,
            activePlayers = activePlayers,
            onExitGame = onExitGame,
            onRequestRestart = { showRestartDialog = true }
        )

        2, 3, 4 -> {
            // Handle the overlaying UI for 2, 3, and 4 player
            // here because it is identical
            Box(modifier = Modifier.fillMaxSize()) {
                // Draw the players
                when (playerCount) {
                    2 -> TwoPlayerScreen(viewModel, activeGameState!!, activePlayers)
                    3 -> ThreePlayerScreen(viewModel, activeGameState!!, activePlayers)
                    4 -> FourPlayerScreen(viewModel, activeGameState!!, activePlayers)
                }

                // Overlay Extra Buttons (Only when paused)
                if (activeGameState!!.isGamePaused) {

                    // Home Button (Left)
                    FloatingActionButton(
                        onClick = onExitGame,
                        modifier = Modifier.align(Alignment.Center).offset(x = (-96).dp),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }

                    // Restart Button (Right)
                    FloatingActionButton(
                        onClick = { showRestartDialog = true },
                        modifier = Modifier.align(Alignment.Center).offset(x = 96.dp),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart Game")
                    }
                }

                // Draw the Play/Pause buttons no matter what
                PlayPauseButton(
                    isGamePaused = activeGameState!!.isGamePaused,
                    onClick = { viewModel.toggleGlobalPlayPause() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        // We couldn't retrieve the player count for some reason
        else -> LoadingErrorScreen(onExitGame)
    }

    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text("Restart Game") },
            text = { Text("Are you sure you want to restart the game? All current progress will be lost.") },
            confirmButton = { Button(onClick = { viewModel.restartGame(); showRestartDialog = false }) { Text("Restart") } },
            dismissButton = { TextButton(onClick = { showRestartDialog = false }) { Text("Cancel") } }
        )
    }
}