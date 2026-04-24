package edu.gvsu.cis.multi_timer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToActiveGame: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditPlaysets: (Int?) -> Unit
) {
    val hasActiveGame by viewModel.hasActiveGame.collectAsState()
    val playsets by viewModel.playsets.collectAsState()

    // Control switches for the two different dialogs
    var showPlaysetSelector by remember { mutableStateOf(false) }
    var showManageSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MultiTimer") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (hasActiveGame) {
                Button(
                    onClick = onNavigateToActiveGame,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("Resume Active Game", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Start Game triggers the Play dialog
            Button(
                onClick = { showPlaysetSelector = true },
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Text("Start New Game", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Manage triggers the Edit dialog
            OutlinedButton(
                onClick = { showManageSelector = true },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Manage Playsets")
            }
        }
    }

    // Start Game
    if (showPlaysetSelector) {
        AlertDialog(
            onDismissRequest = { showPlaysetSelector = false },
            title = { Text("Select a Playset") },
            text = {
                if (playsets.isEmpty()) {
                    Text("No playsets available. Please create one first.")
                } else {
                    LazyColumn {
                        items(playsets) { playset ->
                            TextButton(
                                onClick = {
                                    showPlaysetSelector = false
                                    viewModel.startNewGame(playset) {
                                        onNavigateToActiveGame()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Text("${playset.name} (${playset.playerCount} Players)")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (playsets.isEmpty()) {
                    Button(
                        onClick = {
                            showPlaysetSelector = false
                            onNavigateToEditPlaysets(null) // Null = Create New
                        }
                    ) {
                        Text("Create Playset")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showPlaysetSelector = false }) { Text("Cancel") }
            }
        )
    }

    // Manage / Edit Playsets
    if (showManageSelector) {
        AlertDialog(
            onDismissRequest = { showManageSelector = false },
            title = { Text("Manage Playsets") },
            text = {
                if (playsets.isEmpty()) {
                    Text("No playsets available. Please create one first.")
                } else {
                    LazyColumn {
                        items(playsets) { playset ->
                            TextButton(
                                onClick = {
                                    showManageSelector = false
                                    onNavigateToEditPlaysets(playset.playsetID)
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Text("Edit ${playset.name}")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showManageSelector = false
                        onNavigateToEditPlaysets(null)
                    }
                ) {
                    Text(if (playsets.isEmpty()) "Create Playset" else "Create New")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManageSelector = false }) { Text("Cancel") }
            }
        )
    }
}