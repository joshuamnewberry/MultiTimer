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

        dao.insertPlayset(
            Playset(
                name = "Standard Stopwatch",
                playerCount = 1,
                counterTypesJson = "STOPWATCH",
                autoAdvance = AutoAdvanceConfiguration(
                    enabled = true,
                    reversed = false,
                    inversed = false
                )
            )
        )

        // Create Default Playsets
        dao.insertPlayset(
            Playset(
                name = "5 min Chess",
                playerCount = 2,
                counterTypesJson = "TIMER,TIMER",
                autoAdvance = AutoAdvanceConfiguration(
                    enabled = true,
                    reversed = false,
                    inversed = false
                )
            )
        )

        // Create Default Playsets
        dao.insertPlayset(
            Playset(
                name = "3 min + 2 sec Chess",
                playerCount = 2,
                counterTypesJson = "TIMER,TIMER",
                autoAdvance = AutoAdvanceConfiguration(
                    enabled = true,
                    reversed = false,
                    inversed = false
                ),
                startingTimerSeconds = 3 * 60,
                incrementSeconds = 2
            )
        )

        dao.insertPlayset(
            Playset(
                name = "Magic the Gathering Life Counter",
                playerCount = 3,
                counterTypesJson = "LIFE,LIFE,LIFE",
                // Life counters cannot auto-advance
                autoAdvance = AutoAdvanceConfiguration(
                    enabled = false,
                    reversed = false,
                    inversed = false)
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
    fun startNewGame(playset: Playset, onNavigate: () -> Unit) {
        viewModelScope.launch {
            val modes = playset.counterTypesJson.split(",").mapNotNull {
                try { CounterMode.valueOf(it) } catch (_: Exception) { null }
            }

            val allLife: Boolean = modes.all {it == CounterMode.LIFE }

            val initialPlayersState = List(playset.playerCount) { index ->
                val playerMode = modes.getOrElse(index) { CounterMode.TIMER }

                val startingValue = when (playerMode) {
                    CounterMode.TIMER, CounterMode.STOPWATCH -> playset.startingTimerSeconds * 1000L
                    CounterMode.LIFE -> playset.startingLife.toLong()
                }

                PlayerActiveState(
                    playerProfileId = 1,
                    mode = playerMode,
                    currentValue = startingValue,
                    isRunning = allLife,
                    isCurrentTurn = playset.autoAdvance.enabled && index == 0
                )
            }

            val newGameState = ActiveGameState(
                currentPlayset = playset,
                currentPlayersState = initialPlayersState,
                isGamePaused = allLife,
                hasGameStarted = allLife
            )

            dao.upsertActiveGame(newGameState)
            onNavigate()
        }
    }
}