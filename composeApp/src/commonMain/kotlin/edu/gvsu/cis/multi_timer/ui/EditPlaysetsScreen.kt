package edu.gvsu.cis.multi_timer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.data.CounterMode
import edu.gvsu.cis.multi_timer.viewModels.EditPlaysetsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaysetsScreen(
    viewModel: EditPlaysetsViewModel,
    onBack: () -> Unit
) {
    // Get current values
    val name by viewModel.name.collectAsState()
    val playerCount by viewModel.playerCount.collectAsState()
    val counterTypes by viewModel.counterTypes.collectAsState()
    val autoAdvance by viewModel.autoAdvance.collectAsState()
    val incrementSeconds by viewModel.incrementSeconds.collectAsState()
    val startingLife by viewModel.startingLife.collectAsState()
    val startingTimerSeconds by viewModel.startingTimerSeconds.collectAsState()

    // UI variables
    val hasLife = counterTypes.contains(CounterMode.LIFE)
    val hasTimer = counterTypes.contains(CounterMode.TIMER)

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Playset",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Discard and Go Back", tint = Color.Green)
                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color.Black,
                    scrolledContainerColor = Color.Black,
                    navigationIconContentColor = Color.Green,
                    titleContentColor = Color.Green,
                    actionIconContentColor = Color.Green,
                    subtitleContentColor = Color.Green
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
            // Playset Name
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Playset Name", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = Color.Green
                )
            )

            // Set the player count
            Column {
                Text("Player Count: $playerCount", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Slider(
                    value = playerCount.toFloat(),
                    onValueChange = { viewModel.updatePlayerCount(it.roundToInt()) },
                    valueRange = 1f..4f,
                    steps = 2,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Green,
                        activeTrackColor = Color.Green,
                        inactiveTrackColor = Color.DarkGray,
                        activeTickColor = Color.Black,
                        inactiveTickColor = Color.Gray
                    )
                )
            }

            // Set the counter type
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Counter Modes", style = MaterialTheme.typography.titleMedium, color = Color.White)
                counterTypes.forEachIndexed { index, mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Player ${index + 1}: ", modifier = Modifier.weight(1f), color = Color.LightGray)
                        CounterModeSelection(
                            selectedMode = mode,
                            onModeSelected = { viewModel.updateCounterType(index, it) }
                        )
                    }
                }
            }

            HorizontalDivider(color = Color.DarkGray)

            // Conditional sliders if available
            if (hasTimer) {
                // Set the timer starting time
                Column {
                    Text("Starting Time (Seconds): $startingTimerSeconds", color = Color.White)
                    Slider(
                        value = startingTimerSeconds.toFloat(),
                        onValueChange = { viewModel.updateStartingTimerSeconds(it.roundToInt()) },
                        valueRange = 10f..3600f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Cyan,
                            activeTrackColor = Color.Cyan,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }
            }

            if (hasLife) {
                // Set the life starting ammount
                Column {
                    Text("Starting Life: $startingLife", color = Color.White)
                    Slider(
                        value = startingLife.toFloat(),
                        onValueChange = { viewModel.updateStartingLife(it.roundToInt()) },
                        valueRange = 1f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(1f, .5f, .2f), // Orange to match the Resume button accent
                            activeTrackColor = Color(1f, .5f, .2f),
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }
            }

            // Add a divider if a slider is active
            if (hasTimer || hasLife) {
                HorizontalDivider(color = Color.DarkGray)
            }

            // Enable auto advance
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Auto-Advance", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enabled (Disabled if Life exists)", color = Color.LightGray)
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = autoAdvance.enabled,
                        onCheckedChange = { viewModel.updateAutoAdvance(autoAdvance.copy(enabled = it)) },
                        enabled = !hasLife,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color.Green,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray,
                            disabledCheckedTrackColor = Color.DarkGray,
                            disabledUncheckedTrackColor = Color(0xFF111111)
                        )
                    )
                }

                // Set auto advance values if enabled
                if (autoAdvance.enabled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reversed Order", color = Color.LightGray)
                        Spacer(Modifier.weight(1f))
                        Switch(
                            checked = autoAdvance.reversed,
                            onCheckedChange = { viewModel.updateAutoAdvance(autoAdvance.copy(reversed = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color.Blue,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.DarkGray
                            )
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Inversed (Other's clocks run on your turn)", color = Color.LightGray)
                        Spacer(Modifier.weight(1f))
                        Switch(
                            checked = autoAdvance.inversed,
                            onCheckedChange = { viewModel.updateAutoAdvance(autoAdvance.copy(inversed = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color.Cyan,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.DarkGray
                            )
                        )
                    }
                    Column {
                        Text("Increment Time (Seconds): $incrementSeconds", color = Color.White)
                        Slider(
                            value = incrementSeconds.toFloat(),
                            onValueChange = { viewModel.updateIncrementSeconds(it.roundToInt()) },
                            valueRange = 0f..60f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.Blue,
                                activeTrackColor = Color.Blue,
                                inactiveTrackColor = Color.DarkGray
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Discard or Save the playset
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
                    onClick = { viewModel.savePlayset(onSaved = onBack) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.Black)
                ) {
                    Text("Save Playset", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CounterModeSelection(selectedMode: CounterMode, onModeSelected: (CounterMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        // Show current value
        OutlinedButton(
            onClick = { expanded = true },
            border = BorderStroke(1.dp, Color.DarkGray),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text(selectedMode.name)
        }
        // Set new value
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF222222))
        ) {
            CounterMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.name, color = Color.White) },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}