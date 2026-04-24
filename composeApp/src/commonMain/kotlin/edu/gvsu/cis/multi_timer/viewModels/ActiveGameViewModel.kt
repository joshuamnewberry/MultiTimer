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
                    val updatedPlayers = state.currentPlayersState.map { player ->
                        if (player.isRunning) {
                            stateChanged = true
                            when (player.mode) {
                                CounterMode.TIMER -> player.copy(currentValue = maxOf(0L, player.currentValue - 100))
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

    fun handleInteraction(
        playerId: Int,
        config: AutoAdvanceConfiguration,
        incrementMs: Long = 0L
    ) {
        val currentState = _gameState.value ?: return
        val players = currentState.currentPlayersState.toMutableList()
        val tappedPlayerIndex = players.indexOfFirst { it.playerId == playerId }

        if (tappedPlayerIndex == -1) return
        val tappedPlayer = players[tappedPlayerIndex]

        if (tappedPlayer.mode == CounterMode.LIFE) {
            return
        }

        // Rule: If it's not their turn, ignore the tap completely
        if (!tappedPlayer.isCurrentTurn) return

        if (!config.enabled) {
            // Free-for-all mode: Just toggle this specific player's clock
            players[tappedPlayerIndex] = tappedPlayer.copy(
                isRunning = !tappedPlayer.isRunning
            )
        } else {
            // AutoAdvance is enabled.

            // Find the next player
            val nextIndex = if (config.reversed) {
                if (tappedPlayerIndex - 1 < 0) players.size - 1 else tappedPlayerIndex - 1
            } else {
                (tappedPlayerIndex + 1) % players.size
            }
            val nextPlayer = players[nextIndex]

            // End the current player's turn, add increment, and flip their clock state
            players[tappedPlayerIndex] = tappedPlayer.copy(
                currentValue = tappedPlayer.currentValue + incrementMs,
                isCurrentTurn = false,
                isRunning = !tappedPlayer.isRunning
            )

            // Start the next player's turn and flip their clock state
            players[nextIndex] = nextPlayer.copy(
                isCurrentTurn = true,
                isRunning = !nextPlayer.isRunning
            )
        }

        // Update state and save to DB since turn changed
        val newState = currentState.copy(currentPlayersState = players)
        _gameState.value = newState
        saveStateToDatabase(newState)
    }

    fun updateLife(playerId: Int, amount: Long) {
        val currentState = _gameState.value ?: return
        val players = currentState.currentPlayersState.toMutableList()
        val playerIndex = players.indexOfFirst { it.playerId == playerId }

        if (playerIndex != -1) {
            val player = players[playerIndex]
            players[playerIndex] = player.copy(currentValue = player.currentValue + amount)

            val newState = currentState.copy(currentPlayersState = players)
            _gameState.value = newState
            saveStateToDatabase(newState)
        }
    }

    fun pauseAll() {
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