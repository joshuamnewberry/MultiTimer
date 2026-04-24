package edu.gvsu.cis.multi_timer.ui.playsetScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun TwoPlayerScreen(
    viewModel: ActiveGameViewModel,
    activeGameState: ActiveGameState,
    modifier: Modifier = Modifier.fillMaxSize(),
    topPlayerIndex: Int = 1,
    bottomPlayerIndex: Int = 0,
    topPlayerRotation: Float = 180f,
    bottomPlayerRotation: Float = 0f,
    isHorizontal: Boolean = false
) {
    val playset = activeGameState.currentPlayset
    val config = playset.autoAdvance
    val incrementMs = playset.incrementSeconds * 1000L

    if (isHorizontal) {
        Row(modifier = modifier) {
            PlayerCounterScreen(
                playerState = activeGameState.currentPlayersState[topPlayerIndex],
                isGamePaused = activeGameState.isGamePaused,
                onClick = { viewModel.handleInteraction(topPlayerIndex, config, incrementMs) },
                onLifeChange = { amount -> viewModel.updateLife(topPlayerIndex, amount) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                rotation = topPlayerRotation
            )
            VerticalDivider(thickness = 8.dp, color = Color.Black)
            PlayerCounterScreen(
                playerState = activeGameState.currentPlayersState[bottomPlayerIndex],
                isGamePaused = activeGameState.isGamePaused,
                onClick = { viewModel.handleInteraction(bottomPlayerIndex, config, incrementMs) },
                onLifeChange = { amount -> viewModel.updateLife(bottomPlayerIndex, amount) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                rotation = bottomPlayerRotation
            )
        }
    } else {
        Column(modifier = modifier) {
            PlayerCounterScreen(
                playerState = activeGameState.currentPlayersState[topPlayerIndex],
                isGamePaused = activeGameState.isGamePaused,
                onClick = { viewModel.handleInteraction(topPlayerIndex, config, incrementMs) },
                onLifeChange = { amount -> viewModel.updateLife(topPlayerIndex, amount) },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                rotation = topPlayerRotation
            )
            HorizontalDivider(thickness = 8.dp, color = Color.Black)
            PlayerCounterScreen(
                playerState = activeGameState.currentPlayersState[bottomPlayerIndex],
                isGamePaused = activeGameState.isGamePaused,
                onClick = { viewModel.handleInteraction(bottomPlayerIndex, config, incrementMs) },
                onLifeChange = { amount -> viewModel.updateLife(bottomPlayerIndex, amount) },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                rotation = bottomPlayerRotation
            )
        }
    }
}