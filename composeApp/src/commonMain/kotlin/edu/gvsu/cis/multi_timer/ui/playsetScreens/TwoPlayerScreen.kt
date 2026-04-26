package edu.gvsu.cis.multi_timer.ui.playsetScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.data.ActiveGameState
import edu.gvsu.cis.multi_timer.data.Player
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun TwoPlayerScreen(
    viewModel: ActiveGameViewModel,
    activeGameState: ActiveGameState,
    activePlayers: Map<Int, Player>,
    modifier: Modifier = Modifier.fillMaxSize(),
    topPlayerIndex: Int = 1,
    bottomPlayerIndex: Int = 0,
    topPlayerRotation: Float = 180f,
    bottomPlayerRotation: Float = 0f
) {
    val playset = activeGameState.currentPlayset
    val config = playset.autoAdvance
    val incrementMs = playset.incrementSeconds * 1000L

    Column(modifier = modifier) {
        // Top half
        val pTop = activeGameState.currentPlayersState[topPlayerIndex]
        PlayerCounterScreen(
            playerState = pTop,
            playerProfile = activePlayers[pTop.playerProfileId],
            isGamePaused = activeGameState.isGamePaused,
            isAutoAdvanceEnabled = config.enabled,
            onClick = { viewModel.handleInteraction(topPlayerIndex, config, incrementMs) },
            onLifeChange = { amount -> viewModel.updateLife(topPlayerIndex, amount) },
            modifier = Modifier.weight(1f).fillMaxWidth(),
            rotation = topPlayerRotation
        )

        // Place a divider
        HorizontalDivider(thickness = 8.dp, color = Color.Black)

        // Bottom half
        val pBot = activeGameState.currentPlayersState[bottomPlayerIndex]
        PlayerCounterScreen(
            playerState = pBot,
            playerProfile = activePlayers[pBot.playerProfileId],
            isGamePaused = activeGameState.isGamePaused,
            isAutoAdvanceEnabled = config.enabled,
            onClick = { viewModel.handleInteraction(bottomPlayerIndex, config, incrementMs) },
            onLifeChange = { amount -> viewModel.updateLife(bottomPlayerIndex, amount) },
            modifier = Modifier.weight(1f).fillMaxWidth(),
            rotation = bottomPlayerRotation
        )
    }
}