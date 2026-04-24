package edu.gvsu.cis.multi_timer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.multi_timer.data.ActiveGameState
import edu.gvsu.cis.multi_timer.data.AppDAO
import edu.gvsu.cis.multi_timer.data.AutoAdvanceConfiguration
import edu.gvsu.cis.multi_timer.data.CounterMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ActiveGameViewModel(private val dao: AppDAO) : ViewModel() {

    private val _gameState = MutableStateFlow<ActiveGameState?>(null)
    val gameState: StateFlow<ActiveGameState?> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val initialState = dao.getActiveGame().firstOrNull()
            if (initialState != null) {
                _gameState.value = initialState
                startTimerLoop()
            }
        }
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100)

                _gameState.value?.let { state ->
                    var stateChanged = false
                    if(!state.isGamePaused) {
                        val updatedPlayers = state.currentPlayersState.map { player ->
                            if (player.isRunning) {
                                stateChanged = true
                                when (player.mode) {
                                    CounterMode.TIMER -> player.copy(
                                        currentValue = maxOf(
                                            0L,
                                            player.currentValue - 100
                                        )
                                    )

                                    CounterMode.STOPWATCH -> player.copy(currentValue = player.currentValue + 100)
                                    CounterMode.LIFE -> player
                                }
                            } else {
                                player
                            }
                        }
                        if (stateChanged) {
                            _gameState.value = state.copy(currentPlayersState = updatedPlayers)
                        }
                    }
                }
            }
        }
    }

    fun toggleGlobalPlayPause() {
        val state = _gameState.value ?: return
        val config = state.currentPlayset.autoAdvance
        val players = state.currentPlayersState.toMutableList()

        val isNowPaused = !state.isGamePaused

        // If we are unpausing for the very first time using the Play button
        if (!isNowPaused && !state.hasGameStarted) {
            if (!config.enabled) {
                // Free-for-all: Start all non-life clocks simultaneously
                for (i in players.indices) {
                    if (players[i].mode != CounterMode.LIFE) {
                        players[i] = players[i].copy(isRunning = true)
                    }
                }
            } else {
                // AutoAdvance: Start ONLY the clock for the player whose turn it is
                val activeIndex = players.indexOfFirst { it.isCurrentTurn }
                if (activeIndex != -1 && players[activeIndex].mode != CounterMode.LIFE) {
                    players[activeIndex] = players[activeIndex].copy(isRunning = true)
                }
            }
        }

        val newState = state.copy(
            isGamePaused = isNowPaused,
            hasGameStarted = true, // Officially started!
            currentPlayersState = players
        )
        _gameState.value = newState
        saveStateToDatabase(newState)
    }

    fun handleInteraction(index: Int, config: AutoAdvanceConfiguration, incrementMs: Long = 0L) {
        val currentState = _gameState.value ?: return
        val players = currentState.currentPlayersState.toMutableList()

        if (index !in players.indices) return
        val tappedPlayer = players[index]

        if (tappedPlayer.mode == CounterMode.LIFE) return

        // If it's a Mid-Game Pause, ignore all taps.
        if (currentState.isGamePaused && currentState.hasGameStarted) return

        // If it's the First Start and they tapped a clock INSTEAD of the play button
        if (!currentState.hasGameStarted) {
            if (!config.enabled) {
                // Free-for-all: Only start the specific clock they tapped
                players[index] = tappedPlayer.copy(isRunning = true)
                val newState = currentState.copy(
                    isGamePaused = false,
                    hasGameStarted = true,
                    currentPlayersState = players
                )
                _gameState.value = newState
                saveStateToDatabase(newState)
            } else {
                // AutoAdvance: Override the default starting player based on who was tapped
                val previousTurnIndex = players.indexOfFirst { it.isCurrentTurn }
                if (previousTurnIndex != -1 && previousTurnIndex != index) {
                    players[previousTurnIndex] = players[previousTurnIndex].copy(isCurrentTurn = false)
                }

                players[index] = tappedPlayer.copy(
                    isCurrentTurn = true,
                    isRunning = true
                )

                val newState = currentState.copy(
                    isGamePaused = false,
                    hasGameStarted = true,
                    currentPlayersState = players
                )
                _gameState.value = newState
                saveStateToDatabase(newState)
            }
            return
        }

        // Normal Active Game Logic
        if (!tappedPlayer.isCurrentTurn && config.enabled) return

        if (!config.enabled) {
            players[index] = tappedPlayer.copy(isRunning = !tappedPlayer.isRunning)
        } else {
            if (!tappedPlayer.isRunning) {
                players[index] = tappedPlayer.copy(isRunning = true)
            } else {
                val nextIndex = if (config.reversed) {
                    if (index - 1 < 0) players.size - 1 else index - 1
                } else {
                    (index + 1) % players.size
                }

                players[index] = tappedPlayer.copy(
                    currentValue = tappedPlayer.currentValue + incrementMs,
                    isCurrentTurn = false,
                    isRunning = false
                )

                players[nextIndex] = players[nextIndex].copy(
                    isCurrentTurn = true,
                    isRunning = true
                )
            }
        }
        val newState = currentState.copy(currentPlayersState = players)
        _gameState.value = newState
        saveStateToDatabase(newState)
    }

    fun updateLife(index: Int, amount: Long) {
        val currentState = _gameState.value ?: return

        // If the game has started but is paused, block life changes.
        if (currentState.hasGameStarted && currentState.isGamePaused) return

        val players = currentState.currentPlayersState.toMutableList()
        if (index in players.indices) {
            val player = players[index]
            players[index] = player.copy(currentValue = player.currentValue + amount)

            val newState = currentState.copy(currentPlayersState = players)
            _gameState.value = newState
            saveStateToDatabase(newState)
        }
    }

    fun stopAll() {
        val currentState = _gameState.value ?: return
        val pausedPlayers = currentState.currentPlayersState.map { it.copy(isRunning = false) }
        val newState = currentState.copy(currentPlayersState = pausedPlayers)

        _gameState.value = newState
        saveStateToDatabase(newState)
    }

    private fun saveStateToDatabase(state: ActiveGameState) {
        viewModelScope.launch {
            dao.upsertActiveGame(state)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _gameState.value?.let { saveStateToDatabase(it) }
    }
}