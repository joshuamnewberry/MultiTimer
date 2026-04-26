package edu.gvsu.cis.multi_timer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.viewModels.EditPlayersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlayersScreen(
    viewModel: EditPlayersViewModel,
    onBack: () -> Unit
) {
    // Get current values
    val name by viewModel.name.collectAsState()
    val red by viewModel.redColor.collectAsState()
    val green by viewModel.greenColor.collectAsState()
    val blue by viewModel.blueColor.collectAsState()

    // Live preview of the constructed color
    val previewColor = Color(red = red, green = green, blue = blue, alpha = 1f)

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Player",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Discard and Go Back", tint = Color.Cyan)
                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color.Black,
                    scrolledContainerColor = Color.Black,
                    navigationIconContentColor = Color.Cyan,
                    titleContentColor = Color.Cyan,
                    actionIconContentColor = Color.Cyan,
                    subtitleContentColor = Color.Cyan
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    modifier = Modifier.padding(top = 12.dp).padding(horizontal = 2.dp),
                    onClick = { }
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Open Camera", tint = Color.White)
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Player Name", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Cyan,
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = Color.Cyan
                    )
                )
            }

            HorizontalDivider(color = Color.DarkGray)

            Text("Player Color", style = MaterialTheme.typography.titleMedium, color = Color.White)

            // Live Color Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(previewColor)
                    .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.5f)), RoundedCornerShape(12.dp))
            )

            // RGB Sliders
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Red", color = Color.White)
                Slider(
                    value = red,
                    onValueChange = { viewModel.updateRed(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Red,
                        activeTrackColor = Color.Red,
                        inactiveTrackColor = Color.DarkGray
                    )
                )

                Text("Green", color = Color.White)
                Slider(
                    value = green,
                    onValueChange = { viewModel.updateGreen(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Green,
                        activeTrackColor = Color.Green,
                        inactiveTrackColor = Color.DarkGray
                    )
                )

                Text("Blue", color = Color.White)
                Slider(
                    value = blue,
                    onValueChange = { viewModel.updateBlue(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Blue,
                        activeTrackColor = Color.Blue,
                        inactiveTrackColor = Color.DarkGray
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = BorderStroke(2.dp, Color.Gray)
                ) {
                    Text("Discard", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.savePlayer(onSaved = onBack) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)
                ) {
                    Text("Save Player", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}