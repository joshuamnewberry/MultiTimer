package edu.gvsu.cis.multi_timer.ui.playsetScreens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.data.ActiveGameState
import edu.gvsu.cis.multi_timer.data.Player
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun FourPlayerScreen(
    viewModel: ActiveGameViewModel,
    activeGameState: ActiveGameState,
    activePlayers: Map<Int, Player>
) {
    Row(modifier = Modifier.fillMaxSize()) {

        // Left Column
        TwoPlayerScreen(
            viewModel = viewModel,
            activeGameState = activeGameState,
            activePlayers = activePlayers,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            topPlayerIndex = 0,
            bottomPlayerIndex = 3,
            topPlayerRotation = 90f,
            bottomPlayerRotation = 90f,
        )

        // Center line down the table
        VerticalDivider(thickness = 8.dp, color = Color.Black)

        // Right Column
        TwoPlayerScreen(
            viewModel = viewModel,
            activeGameState = activeGameState,
            activePlayers = activePlayers,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            topPlayerIndex = 1,
            bottomPlayerIndex = 2,
            topPlayerRotation = -90f,
            bottomPlayerRotation = -90f,
        )
    }
}