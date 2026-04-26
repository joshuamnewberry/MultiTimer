package edu.gvsu.cis.multi_timer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.data.Player
import edu.gvsu.cis.multi_timer.data.Playset
import edu.gvsu.cis.multi_timer.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToActiveGame: () -> Unit,
    onNavigateToEditPlaysets: (Int?) -> Unit,
    onNavigateToEditPlayers: (Int?) -> Unit
) {
    // Get viewModel data
    val hasActiveGame by viewModel.hasActiveGame.collectAsState()
    val playsets by viewModel.playsets.collectAsState()
    val players by viewModel.players.collectAsState()

    // Control switches for the dialogs
    var showPlaysetSelector by remember { mutableStateOf(false) }
    var showManageSelector by remember { mutableStateOf(false) }
    var showPlayerSelector by remember { mutableStateOf(false) }

    // State for the Player Assignment flow
    var showPlayerAssignmentDialog by remember { mutableStateOf(false) }
    var pendingPlayset by remember { mutableStateOf<Playset?>(null) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            // Title
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "MultiTimer",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                },
                colors = TopAppBarColors(
                    containerColor = Color.Black,
                    scrolledContainerColor = Color.Black,
                    navigationIconContentColor = Color.Green,
                    titleContentColor = Color.Green,
                    actionIconContentColor = Color.Green,
                    subtitleContentColor = Color.Green
                ),
                modifier = Modifier.padding(top = 64.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Resume Active Game
            OutlinedButton(
                onClick = onNavigateToActiveGame,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                border = if(hasActiveGame) BorderStroke(2.dp, Color(1f, .5f, .2f))
                else BorderStroke(2.dp, Color(.5f, .5f, .5f)),
                enabled = hasActiveGame
            ) {
                Text(
                    text = "Resume Active Game",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if(hasActiveGame) Color.White else Color(.5f, .5f, .5f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start New Game
            OutlinedButton(
                onClick = { showPlaysetSelector = true },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                border = BorderStroke(2.dp, Color.Green),
            ) {
                Text(
                    text = "Start New Game",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Manage Playsets
            OutlinedButton(
                onClick = { showManageSelector = true },
                modifier = Modifier.fillMaxWidth().height(42.dp),
                border = BorderStroke(2.dp, Color.Blue),
            ) {
                Text(
                    text = "Manage Playsets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Manage Players
            OutlinedButton(
                onClick = { showPlayerSelector = true },
                modifier = Modifier.fillMaxWidth().height(42.dp),
                border = BorderStroke(2.dp, Color.Cyan),
            ) {
                Text(
                    text = "Manage Players",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // Select a Playset
    if (showPlaysetSelector) {
        AlertDialog(
            onDismissRequest = { showPlaysetSelector = false },
            containerColor = Color(0xFF111111),
            titleContentColor = Color.Green,
            textContentColor = Color.White,
            title = { Text("Select a Playset") },
            text = {
                if (playsets.isEmpty()) {
                    Text("No playsets available. Please create one first.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(playsets) { playset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showPlaysetSelector = false
                                        pendingPlayset = playset
                                        showPlayerAssignmentDialog = true
                                    }
                                    .padding(vertical = 16.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${playset.name} (${playset.playerCount} Players)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            },
            // Show option to create new playset
            confirmButton = {
                TextButton(onClick = {
                    showPlaysetSelector = false
                    onNavigateToEditPlaysets(null)
                }) {
                    Text("Create Playset", color = Color.Green)
                }
            },
            // Cancel selecting a playset
            dismissButton = {
                TextButton(onClick = { showPlaysetSelector = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    // Select your players
    if (showPlayerAssignmentDialog && pendingPlayset != null) {
        val playset = pendingPlayset!!

        val selectedPlayers = remember(playset) {
            mutableStateListOf<Player>().apply {
                val defaultPlayer = players.firstOrNull() ?: Player()
                repeat(playset.playerCount) {
                    add(defaultPlayer)
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showPlayerAssignmentDialog = false },
            containerColor = Color(0xFF111111),
            titleContentColor = Color.Green,
            textContentColor = Color.White,
            title = { Text("Select Players") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(playset.playerCount) { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text("Player ${index + 1}: ", modifier = Modifier.weight(1f), color = Color.White)
                            PlayerDropdownMenu(
                                players = players,
                                selectedPlayer = selectedPlayers[index],
                                onPlayerSelected = { selectedPlayers[index] = it }
                            )
                        }
                    }
                }
            },
            // Start the game
            confirmButton = {
                TextButton(onClick = {
                    showPlayerAssignmentDialog = false
                    viewModel.startNewGame(playset, selectedPlayers) {
                        onNavigateToActiveGame()
                    }
                }) {
                    Text("Start Game", color = Color.Green)
                }
            },
            // Cancel selecting players
            dismissButton = {
                TextButton(onClick = { showPlayerAssignmentDialog = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    // Manage your playsets
    if (showManageSelector) {
        AlertDialog(
            onDismissRequest = { showManageSelector = false },
            containerColor = Color(0xFF111111),
            titleContentColor = Color.Blue,
            textContentColor = Color.White,
            title = { Text("Manage Playsets") },
            text = {
                if (playsets.isEmpty()) {
                    Text("No playsets available. Please create one first.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(playsets) { playset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showManageSelector = false
                                        onNavigateToEditPlaysets(playset.playsetID)
                                    }
                                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = playset.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f),
                                    color = Color.White
                                )

                                IconButton(
                                    onClick = { viewModel.deletePlayset(playset) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Playset",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            },
            // Show option to create new playset
            confirmButton = {
                TextButton(onClick = {
                    showManageSelector = false
                    onNavigateToEditPlaysets(null)
                }) {
                    Text(if (playsets.isEmpty()) "Create Playset" else "Create New", color = Color.Blue)
                }
            },
            // Cancel managing playsets
            dismissButton = {
                TextButton(onClick = { showManageSelector = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    // Manage your players
    if (showPlayerSelector) {
        AlertDialog(
            onDismissRequest = { showPlayerSelector = false },
            containerColor = Color(0xFF111111),
            titleContentColor = Color.Cyan,
            textContentColor = Color.White,
            title = { Text("Manage Players") },
            text = {
                if (players.isEmpty()) {
                    Text("No players available.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(players) { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showPlayerSelector = false
                                        onNavigateToEditPlayers(player.playerID)
                                    }
                                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = player.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f),
                                    color = Color.White
                                )

                                // Protect the Default Player (ID 1) from deletion
                                if (player.playerID != 1) {
                                    IconButton(
                                        onClick = { viewModel.deletePlayer(player) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Player",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(48.dp))
                                }
                            }
                        }
                    }
                }
            },
            // Show option to create a new player
            confirmButton = {
                TextButton(onClick = {
                    showPlayerSelector = false
                    onNavigateToEditPlayers(null)
                }) {
                    Text("Create New Player", color = Color.Cyan)
                }
            },
            // Cancel managing your players
            dismissButton = {
                TextButton(onClick = { showPlayerSelector = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }
}

@Composable
fun PlayerDropdownMenu(
    players: List<Player>,
    selectedPlayer: Player,
    onPlayerSelected: (Player) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        // Show current selection
        OutlinedButton(
            onClick = { expanded = true },
            border = BorderStroke(1.dp, Color.DarkGray),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text(selectedPlayer.name)
        }
        // Select a different player
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF222222))
        ) {
            players.forEach { player ->
                DropdownMenuItem(
                    text = { Text(player.name, color = Color.White) },
                    onClick = {
                        onPlayerSelected(player)
                        expanded = false
                    }
                )
            }
        }
    }
}