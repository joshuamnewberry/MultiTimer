package edu.gvsu.cis.multi_timer.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayPauseButton(
    isGamePaused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = if (isGamePaused) Icons.Default.PlayArrow else Icons.Default.Pause,
            contentDescription = if (isGamePaused) "Play Game" else "Pause Game",
            modifier = Modifier.size(36.dp)
        )
    }
}