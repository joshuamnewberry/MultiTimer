package edu.gvsu.cis.multi_timer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.multi_timer.data.ActiveGameState
import edu.gvsu.cis.multi_timer.data.AppDAO
import edu.gvsu.cis.multi_timer.data.AutoAdvanceConfiguration
import edu.gvsu.cis.multi_timer.data.CounterMode
import edu.gvsu.cis.multi_timer.data.Player
import edu.gvsu.cis.multi_timer.data.PlayerActiveState
import edu.gvsu.cis.multi_timer.data.Playset
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val dao: AppDAO) : ViewModel() {

    // Observe if a game is currently in progress
    val hasActiveGame: StateFlow<Boolean> = dao.getActiveGame()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // Observe the list of saved playsets for the Quick Start menu
    val playsets: StateFlow<List<Playset>> = dao.selectAllPlaysets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            // First Run Check
            val currentPlayers = dao.selectAllPlayers().first()

            if (currentPlayers.isEmpty()) {
                initializeDefaults()
            }
        }
    }

    private suspend fun initializeDefaults() {
        // Create the undeletable Default Player.
        dao.insertPlayer(
            Player(
                name = "Player 1",
                playerBackgroundColorArgb = 0xFF448AFF // Blue
            )
        )

        // Create Default Playsets
        dao.insertPlayset(
            Playset(
                name = "Standard Chess",
                playerCount = 2,
                counterTypesJson = "TIMER,TIMER",
                autoAdvance = AutoAdvanceConfiguration(
                    enabled = true,
                    reversed = false,
                    inversed = false
                )
            )
        )

        dao.insertPlayset(
            Playset(
                name = "Magic the Gathering Life Counter",
                playerCount = 4,
                counterTypesJson = "LIFE,LIFE,LIFE,LIFE",
                // Life counters cannot auto-advance
                autoAdvance = AutoAdvanceConfiguration(
                    enabled = false,
                    reversed = false,
                    inversed = false)
            )
        )
    }

    // Initialize a new game state and overwrite the old one
    fun startNewGame(playset: Playset, onGameReady: () -> Unit) {
        viewModelScope.launch {

            val initialPlayers = List(playset.playerCount) { index ->
                val isFirstPlayer = (index == 0)

                // THE INITIALIZATION TRICK
                val isTurn = if (!playset.autoAdvance.enabled) {
                    true // Free-for-all: everyone's turn is always true
                } else {
                    isFirstPlayer // AutoAdvance: only Player 1 starts with the turn
                }

                val running = if (!playset.autoAdvance.enabled) {
                    false // Start paused in free-for-all
                } else if (playset.autoAdvance.inversed) {
                    !isFirstPlayer // Inversed: everyone runs except the first player
                } else {
                    isFirstPlayer // Standard: only first player runs
                }

                PlayerActiveState(
                    playerId = index,
                    mode = CounterMode.TIMER,
                    currentValue = 300000L,
                    isRunning = running,
                    isCurrentTurn = isTurn
                )
            }

            val newGame = ActiveGameState(
                currentPlayset = playset,
                currentPlayersState = initialPlayers,
                activeGameStateID = 0
            )

            dao.upsertActiveGame(newGame)
            onGameReady()
        }
    }
}