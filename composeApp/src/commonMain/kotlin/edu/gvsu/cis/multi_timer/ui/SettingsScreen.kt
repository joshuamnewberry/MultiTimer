package edu.gvsu.cis.multi_timer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.multi_timer.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val syncKey by viewModel.syncKey.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    var importKeyInput by remember { mutableStateOf("") }

    // Control switches for the dialogs
    var showConfirmDownload by remember { mutableStateOf(false) }

    // Load the key as soon as the screen opens
    LaunchedEffect(Unit) {
        viewModel.loadSyncKey()
        importKeyInput = viewModel.syncKey.value
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cloud Sync",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Magenta
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back", tint = Color.Magenta)
                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color.Black,
                    scrolledContainerColor = Color.Black,
                    navigationIconContentColor = Color.Magenta,
                    titleContentColor = Color.Magenta,
                    actionIconContentColor = Color.Magenta,
                    subtitleContentColor = Color.Magenta
                ),
                modifier = Modifier.padding(top = 64.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 32.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Backup
            Text(
                text = "Your Sync Key",
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = syncKey,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Push Button
            OutlinedButton(
                onClick = { viewModel.pushBackupToCloud() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                border = BorderStroke(2.dp, Color.Cyan),
                enabled = !isSyncing && syncKey.isNotEmpty()
            ) {
                Text(
                    text = if (isSyncing) "Syncing..." else "Push Backup to Cloud",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.DarkGray)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Download
            Text(
                text = "Import Backup",
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = importKeyInput,
                onValueChange = { importKeyInput = it.uppercase() },
                placeholder = { Text("Enter Sync Key", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Magenta,
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = Color.Magenta
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Pull Button
            OutlinedButton(
                onClick = {
                    showConfirmDownload = true
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                border = BorderStroke(2.dp, Color.Magenta),
                enabled = !isSyncing && importKeyInput.length >= 6
            ) {
                Text(
                    text = "Pull Backup from Cloud",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // Manage your playsets
    if (showConfirmDownload) {
        AlertDialog(
            onDismissRequest = { showConfirmDownload = false },
            containerColor = Color(0xFF111111),
            titleContentColor = Color.Green,
            textContentColor = Color.White,
            title = { Text("Are you sure?") },
            text = {
                Text("This will overwrite your currently saved players and playsets!")
            },
            // Show option to create new playset
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDownload = false
                    viewModel.pullBackupFromCloud(importKeyInput) {
                        importKeyInput = "" // clear input on success
                    }
                }) {
                    Text("Confirm", color = Color.Green)
                }
            },
            // Cancel managing playsets
            dismissButton = {
                TextButton(onClick = { showConfirmDownload = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }
}