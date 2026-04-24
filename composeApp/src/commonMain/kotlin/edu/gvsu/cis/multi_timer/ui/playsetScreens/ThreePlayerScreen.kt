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
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun ThreePlayerScreen(viewModel: ActiveGameViewModel, activeGameState: ActiveGameState) {
    val playset = activeGameState.currentPlayset
    val config = playset.autoAdvance
    val incrementMs = playset.incrementSeconds * 1000L

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            PlayerCounterScreen(
                playerState = activeGameState.currentPlayersState[1], // Player 2
                isGamePaused = activeGameState.isGamePaused,
                onClick = { viewModel.handleInteraction(1, config, incrementMs) },
                onLifeChange = { amount -> viewModel.updateLife(1, amount) },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                rotation = 90f
            )
            HorizontalDivider(thickness = 8.dp, color = Color.Black)
            PlayerCounterScreen(
                playerState = activeGameState.currentPlayersState[0], // Player 1
                isGamePaused = activeGameState.isGamePaused,
                onClick = { viewModel.handleInteraction(0, config, incrementMs) },
                onLifeChange = { amount -> viewModel.updateLife(0, amount) },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                rotation = 90f
            )
        }

        VerticalDivider(thickness = 8.dp, color = Color.Black)

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Spacer(modifier = Modifier.weight(1f))
            PlayerCounterScreen(
                playerState = activeGameState.currentPlayersState[2], // Player 3
                isGamePaused = activeGameState.isGamePaused,
                onClick = { viewModel.handleInteraction(2, config, incrementMs) },
                onLifeChange = { amount -> viewModel.updateLife(2, amount) },
                modifier = Modifier.weight(2f).fillMaxWidth(),
                rotation = -90f
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}