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
    // Current Game
    private val _gameState = MutableStateFlow<ActiveGameState?>(null)
    val gameState: StateFlow<ActiveGameState?> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    // Current Players
    private val _activePlayers = MutableStateFlow<Map<Int, edu.gvsu.cis.multi_timer.data.Player>>(emptyMap())
    val activePlayers: StateFlow<Map<Int, edu.gvsu.cis.multi_timer.data.Player>> = _activePlayers.asStateFlow()

    // Start game in start or paused state by conditions
    // Fetch players from game data
    init {
        viewModelScope.launch {
            val initialState = dao.getActiveGame().firstOrNull()
            if (initialState != null) {
                // Fetch the actual Player profiles from the database using the playerProfileId
                val playersMap = mutableMapOf<Int, edu.gvsu.cis.multi_timer.data.Player>()
                initialState.currentPlayersState.forEach { pState ->
                    val player = dao.getPlayerById(pState.playerProfileId)
                    if (player != null) {
                        playersMap[pState.playerProfileId] = player
                    }
                }
                _activePlayers.value = playersMap

                // Resume logic
                val allLife = initialState.currentPlayersState.all { it.mode == CounterMode.LIFE }
                val pausedState = if (allLife) {
                    initialState.copy(isGamePaused = false, hasGameStarted = true)
                } else {
                    initialState.copy(isGamePaused = true)
                }

                _gameState.value = pausedState
                saveStateToDatabase(pausedState)
                startTimerLoop()
            }
        }
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(10)

                // Hold onto the game state for this section
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
                                            player.currentValue - 10
                                        )
                                    )

                                    CounterMode.STOPWATCH -> player.copy(currentValue = player.currentValue + 10)
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
        // Grab current values
        val state = _gameState.value ?: return
        val config = state.currentPlayset.autoAdvance

        // Current Pause state
        val isNowPaused = state.isGamePaused

        // Create variable
        var newState: ActiveGameState?

        // If we are unpaused or already started
        if (!isNowPaused || state.hasGameStarted) {
            // Save the game state with the flipped pause state
            newState = state.copy(
                isGamePaused = !isNowPaused
            )
        }
        else {
            // If we are paused and haven't started
            val players = state.currentPlayersState.toMutableList()
            // If we are running auto advance
            if (!config.enabled) {
                // Start all non-life clocks simultaneously
                for (i in players.indices) {
                    if (players[i].mode != CounterMode.LIFE) {
                        players[i] = players[i].copy(isRunning = true)
                    }
                }
                // Save the game state with the flipped pause state
                newState = state.copy(
                    isGamePaused = !isNowPaused,
                    hasGameStarted = true,
                    currentPlayersState = players
                )
            }
            else {
                // AutoAdvance: Start only one, or all but one clocks simultaneously
                val activeIndex = players.indexOfFirst { it.isCurrentTurn }
                if (activeIndex != -1) {
                    for (i in players.indices) {
                        if (players[i].mode == CounterMode.LIFE) {
                            players[i] = players[i].copy(isRunning = false)
                            continue
                        }
                        players[i] = players[i].copy(
                            isRunning = if (config.inversed) i != activeIndex else i == activeIndex
                        )
                    }
                }
                newState = state.copy(
                    isGamePaused = !isNowPaused,
                    hasGameStarted = true,
                    currentPlayersState = players
                )
            }
        }
        // Update globally and save
        _gameState.value = newState
        saveStateToDatabase(newState)
    }

    fun handleInteraction(index: Int, config: AutoAdvanceConfiguration, incrementMs: Long = 0L) {
        // Get current state
        val currentState = _gameState.value ?: return

        //  If it's a Mid-Game Pause, ignore all taps.
        if (currentState.isGamePaused && currentState.hasGameStarted) return

        // Get player list
        val players = currentState.currentPlayersState.toMutableList()

        // Avoid index out of bounds error
        if (index !in players.indices) return

        // Get current player
        val tappedPlayer = players[index]

        // Ignore LIFE mode player
        if (tappedPlayer.mode == CounterMode.LIFE) return

        // First Start
        if (!currentState.hasGameStarted) {
            // If Auto advance disabled start the tapped player and start game
            if (!config.enabled) {
                players[index] = tappedPlayer.copy(isRunning = true)
                val newState = currentState.copy(
                    isGamePaused = false,
                    hasGameStarted = true,
                    currentPlayersState = players
                )
                _gameState.value = newState
                saveStateToDatabase(newState)
                return
            }
            // If Auto advance enabled override the default starting player and start game
            val previousTurnIndex = players.indexOfFirst { it.isCurrentTurn }
            if (previousTurnIndex != -1 && previousTurnIndex != index) {
                players[previousTurnIndex] = players[previousTurnIndex].copy(isCurrentTurn = false)
            }

            players[index] = players[index].copy(isCurrentTurn = true)

            for (i in players.indices) {
                if (players[i].mode == CounterMode.LIFE) {
                    players[i] = players[i].copy(isRunning = false)
                    continue
                }
                players[i] = players[i].copy(
                    isRunning = if (config.inversed) i != index else i == index
                )
            }

            val newState = currentState.copy(
                isGamePaused = false,
                hasGameStarted = true,
                currentPlayersState = players
            )
            _gameState.value = newState
            saveStateToDatabase(newState)
            return
        }

        // Normal Active Game Logic

        // Ignore taps if auto advance on and not their turn
        if (!tappedPlayer.isCurrentTurn && config.enabled) return

        // For auto advance off
        if (!config.enabled) {
            players[index] = tappedPlayer.copy(isRunning = !tappedPlayer.isRunning)
        }
        else if(players.size == 1) {
            players[0] = players[0].copy(
                currentValue = players[index].currentValue + if(players[0].isRunning) incrementMs else 0,
                isRunning = !players[0].isRunning
            )
        }
        else {
            // Calculate who gets the turn next
            val nextIndex = if (config.reversed) {
                if (index - 1 < 0) players.size - 1 else index - 1
            } else {
                (index + 1) % players.size
            }

            // Apply the increment to the player ending their turn
            players[index] = players[index].copy(
                currentValue = players[index].currentValue + incrementMs,
                isCurrentTurn = false
            )

            // Give the turn to the new player
            players[nextIndex] = players[nextIndex].copy(
                isCurrentTurn = true
            )

            for (i in players.indices) {
                players[i] = players[i].copy(
                    isRunning = if (config.inversed) i != nextIndex else i == nextIndex
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

    // Re-set up the game
    fun restartGame() {
        val state = _gameState.value ?: return
        val playset = state.currentPlayset

        // Re-read the counter types
        val modes = playset.counterTypesJson.split(",").mapNotNull {
            try { CounterMode.valueOf(it) } catch (_: Exception) { null }
        }

        val allLife: Boolean = modes.all { it == CounterMode.LIFE }

        // Reset all players to their initial values
        val resetPlayers = state.currentPlayersState.mapIndexed { index, player ->
            val playerMode = modes.getOrElse(index) { CounterMode.TIMER }

            val startingValue = when (playerMode) {
                CounterMode.TIMER -> playset.startingTimerSeconds * 1000L
                CounterMode.STOPWATCH -> 0L
                CounterMode.LIFE -> playset.startingLife.toLong()
            }

            player.copy(
                currentValue = startingValue,
                isRunning = allLife,
                isCurrentTurn = !playset.autoAdvance.enabled || index == 0
            )
        }

        // Apply the reset state
        val newState = state.copy(
            currentPlayersState = resetPlayers,
            isGamePaused = !allLife,
            hasGameStarted = allLife
        )

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