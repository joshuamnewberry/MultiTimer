package edu.gvsu.cis.multi_timer.ui.playsetScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.data.ActiveGameState
import edu.gvsu.cis.multi_timer.data.Player
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun ThreePlayerScreen(
    viewModel: ActiveGameViewModel,
    activeGameState: ActiveGameState,
    activePlayers: Map<Int, Player>
) {
    val playset = activeGameState.currentPlayset
    val config = playset.autoAdvance
    val incrementMs = playset.incrementSeconds * 1000L

    Row(modifier = Modifier.fillMaxSize()) {
        // Handled by Two Player Screen
        TwoPlayerScreen(
            viewModel = viewModel,
            activeGameState = activeGameState,
            activePlayers = activePlayers,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            topPlayerIndex = 1,
            bottomPlayerIndex = 0,
            topPlayerRotation = 90f,
            bottomPlayerRotation = 90f,
        )

        // Center line down the table
        VerticalDivider(thickness = 8.dp, color = Color.Black)

        // Right Column
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            // Center the third player
            Spacer(modifier = Modifier.weight(1f))

            // Place a divider before and after the player
            HorizontalDivider(thickness = 8.dp, color = Color.Black)

            // Draw the player
            val p3 = activeGameState.currentPlayersState[2]
            PlayerCounterScreen(
                playerState = p3,
                playerProfile = activePlayers[p3.playerProfileId],
                isGamePaused = activeGameState.isGamePaused,
                isAutoAdvanceEnabled = config.enabled,
                onClick = { viewModel.handleInteraction(2, config, incrementMs) },
                onLifeChange = { amount -> viewModel.updateLife(2, amount) },
                modifier = Modifier.weight(2f).fillMaxWidth(),
                rotation = -90f
            )

            HorizontalDivider(thickness = 8.dp, color = Color.Black)

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}