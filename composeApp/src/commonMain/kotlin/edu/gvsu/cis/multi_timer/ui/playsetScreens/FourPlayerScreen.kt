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
import edu.gvsu.cis.multi_timer.viewModels.ActiveGameViewModel

@Composable
fun FourPlayerScreen(viewModel: ActiveGameViewModel, activeGameState: ActiveGameState) {
    Row(modifier = Modifier.fillMaxSize()) {

        // Left Column (Player 3 on top, Player 1 on bottom)
        TwoPlayerScreen(
            viewModel = viewModel,
            activeGameState = activeGameState,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            topPlayerIndex = 2,
            bottomPlayerIndex = 0,
            topPlayerRotation = 90f, // Faces the left edge
            bottomPlayerRotation = 90f,
            isHorizontal = false // Stacked vertically inside its column
        )

        // Center line down the table
        VerticalDivider(thickness = 8.dp, color = Color.Black)

        // Right Column (Player 4 on top, Player 2 on bottom)
        TwoPlayerScreen(
            viewModel = viewModel,
            activeGameState = activeGameState,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            topPlayerIndex = 3,
            bottomPlayerIndex = 1,
            topPlayerRotation = -90f, // Faces the right edge
            bottomPlayerRotation = -90f,
            isHorizontal = false // Stacked vertically inside its column
        )
    }
}