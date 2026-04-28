package edu.gvsu.cis.multi_timer.ui.playsetScreens

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import edu.gvsu.cis.multi_timer.data.CounterMode
import edu.gvsu.cis.multi_timer.data.Player
import edu.gvsu.cis.multi_timer.data.PlayerActiveState

@Composable
fun PlayerCounterScreen(
    playerState: PlayerActiveState,
    playerProfile: Player?,
    isGamePaused: Boolean,
    isAutoAdvanceEnabled: Boolean,
    onClick: () -> Unit,
    onLifeChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    rotation: Float = 0f
) {
    // Unpack Player Profile Data
    val playerName = playerProfile?.name ?: "Player"
    val isDefaultColor = playerProfile == null || playerProfile.playerBackgroundColor == 0xFFFFFFFFL
    val bgColor = if (isDefaultColor) MaterialTheme.colorScheme.primaryContainer else Color(playerProfile.playerBackgroundColor)
    val contentColor = if (isDefaultColor) MaterialTheme.colorScheme.onPrimaryContainer else if(bgColor.luminance() > .5) Color.Black else Color.White

    // Logic for button indicator
    val showRipple = !isGamePaused && playerState.mode != CounterMode.LIFE &&
            (!isAutoAdvanceEnabled || playerState.isCurrentTurn)

    // Logic for player active/inactive color
    val isInactive = if (isAutoAdvanceEnabled) {
        !playerState.isCurrentTurn
    } else {
        // Life counters shouldn't dim just because they aren't "running"
        !(playerState.isRunning || playerState.mode == CounterMode.LIFE)
    }

    // Logic for turn indicator outline
    val showBorder = isAutoAdvanceEnabled && playerState.isCurrentTurn

    // Apply the specific dimming rules for pause or inactive clocks
    val dimAlpha = if (isGamePaused) 0.5f else if (isInactive) 0.25f else 0f
    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = modifier
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = if (showRipple) LocalIndication.current else null,
                onClick = onClick
            )
    ) {
        // Swap dimensions for players seated on the left/right edges
        val isRotatedSideways = rotation == 90f || rotation == -90f || rotation == 270f || rotation == -270f
        val contentWidth = if (isRotatedSideways) maxHeight else maxWidth
        val contentHeight = if (isRotatedSideways) maxWidth else maxHeight

        // Draw the Opacity Dimming under the content
        if (dimAlpha > 0f) {
            Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = dimAlpha)))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(rotationZ = rotation)
                .wrapContentSize(unbounded = true)
        ) {
            // A Box representing the exact screen boundaries of the player
            Box(modifier = Modifier.size(width = contentWidth, height = contentHeight)) {

                // The main counter
                Box(modifier = Modifier.align(Alignment.Center)) {
                    if (playerState.mode == CounterMode.LIFE) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            IconButton(onClick = { onLifeChange(-1L) }) { Icon(Icons.Default.Remove, "Decrease Life", tint = contentColor) }
                            Text(playerState.currentValue.toString(), fontSize = 80.sp, fontWeight = FontWeight.Bold, color = contentColor, modifier = Modifier.padding(horizontal = 24.dp))
                            IconButton(onClick = { onLifeChange(1L) }) { Icon(Icons.Default.Add, "Increase Life", tint = contentColor) }
                        }
                    } else {
                        val totalSeconds = playerState.currentValue / 1000
                        val minutes = totalSeconds / 60
                        val seconds = totalSeconds % 60

                        val displayString = if (playerState.mode == CounterMode.STOPWATCH) {
                            val hundredths = (playerState.currentValue % 1000) / 10
                            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}.${hundredths.toString().padStart(2, '0')}"
                        } else {
                            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
                        }
                        Text(displayString, fontSize = 80.sp, fontWeight = FontWeight.Bold, color = contentColor)
                    }
                }

                // The Player Profile Tag
                Row(
                    modifier = Modifier.align(Alignment.TopStart)
                        .padding(all = 16.dp)
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Picture
                    ProfilePic(playerName, playerProfile?.profilePicture, contentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = playerName,
                        color = contentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(all = 16.dp)
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer(rotationZ = 180f)
                            .wrapContentSize(unbounded = true)
                    ) {
                        Row {
                            // Profile Picture
                            ProfilePic(playerName, playerProfile?.profilePicture, contentColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = playerName,
                                color = contentColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Draw the Active Turn Border on the absolute top layer
        if (showBorder) {
            Box(modifier = Modifier.matchParentSize().border(2.dp, Color.White))
        }
    }
}

@Composable
fun ProfilePic(name: String, picture: String?, contentColor: Color) {
    Box(
        modifier = Modifier.size(32.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if(picture != null) {
            AsyncImage(
                model = picture,
                contentDescription = "Player Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop,
            )
        }
        else {
            Text(
                name.take(1).uppercase(),
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}