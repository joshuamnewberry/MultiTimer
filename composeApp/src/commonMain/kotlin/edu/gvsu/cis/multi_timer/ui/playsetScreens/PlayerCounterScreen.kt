package edu.gvsu.cis.multi_timer.ui.playsetScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gvsu.cis.multi_timer.data.CounterMode
import edu.gvsu.cis.multi_timer.data.PlayerActiveState

@Composable
fun PlayerCounterScreen(
    playerState: PlayerActiveState,
    isGamePaused: Boolean,
    onClick: () -> Unit,
    onLifeChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    rotation: Float = 0f
) {
    val isActive = !isGamePaused && (
                    playerState.isCurrentTurn ||
                    playerState.isRunning ||
                    playerState.mode == CounterMode.LIFE
            )

    val bgColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(bgColor)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .graphicsLayer(rotationZ = rotation)
                .wrapContentSize(unbounded = true)
        ) {
            if (playerState.mode == CounterMode.LIFE) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { onLifeChange(-1L) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease Life", tint = contentColor)
                    }

                    Text(
                        text = playerState.currentValue.toString(),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    IconButton(onClick = { onLifeChange(1L) }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase Life", tint = contentColor)
                    }
                }
            } else {
                val totalSeconds = playerState.currentValue / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                val displayString = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

                Text(
                    text = displayString,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}