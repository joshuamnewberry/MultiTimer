package edu.gvsu.cis.multi_timer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.multi_timer.sessionManager
import edu.gvsu.cis.multi_timer.data.AppDAO
import edu.gvsu.cis.multi_timer.data.AutoAdvanceConfiguration
import edu.gvsu.cis.multi_timer.data.CounterMode
import edu.gvsu.cis.multi_timer.data.Playset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPlaysetsViewModel(
    private val dao: AppDAO,
    sessionManager: sessionManager
) : ViewModel() {
    private var editingPlaysetId: Int = 0

    private val _name = MutableStateFlow("Playset")
    val name = _name.asStateFlow()

    private val _playerCount = MutableStateFlow(2)
    val playerCount = _playerCount.asStateFlow()

    private val _counterTypes = MutableStateFlow<List<CounterMode>>(List(2) { CounterMode.TIMER })
    val counterTypes = _counterTypes.asStateFlow()

    private val _autoAdvance = MutableStateFlow(AutoAdvanceConfiguration())
    val autoAdvance = _autoAdvance.asStateFlow()

    private val _incrementSeconds = MutableStateFlow(0)
    val incrementSeconds = _incrementSeconds.asStateFlow()

    private val _startingLife = MutableStateFlow(40)
    val startingLife = _startingLife.asStateFlow()

    private val _startingTimerSeconds = MutableStateFlow(300)
    val startingTimerSeconds = _startingTimerSeconds.asStateFlow()

    init {
        val idToEdit = sessionManager.currentEditId
        if (idToEdit != null) {
            loadPlaysetById(idToEdit)
            sessionManager.currentEditId = null
        }
    }

    private fun loadPlaysetById(id: Int) {
        viewModelScope.launch {
            val playset = dao.getPlaysetById(id)
            if (playset != null) {
                // Save the ID for later
                editingPlaysetId = playset.playsetID

                // Unpack variables into distinct flows
                _name.value = playset.name
                _playerCount.value = playset.playerCount
                _autoAdvance.value = playset.autoAdvance
                _incrementSeconds.value = playset.incrementSeconds
                _startingLife.value = playset.startingLife
                _startingTimerSeconds.value = playset.startingTimerSeconds

                // Unpack the JSON list
                val modes = playset.counterTypesJson.split(",").mapNotNull {
                    try { CounterMode.valueOf(it) } catch (_: Exception) { null }
                }
                _counterTypes.value = if (modes.size == playset.playerCount) modes else List(playset.playerCount) { CounterMode.TIMER }

                validateAutoAdvance()
            }
        }
    }

    // Updates from user
    fun updateName(newName: String) { _name.value = newName }

    fun updatePlayerCount(count: Int) {
        _playerCount.value = count
        val currentTypes = _counterTypes.value.toMutableList()
        while (currentTypes.size < count) currentTypes.add(CounterMode.TIMER)
        while (currentTypes.size > count) currentTypes.removeLast()
        _counterTypes.value = currentTypes
        validateAutoAdvance()
    }

    fun updateCounterType(index: Int, mode: CounterMode) {
        val currentTypes = _counterTypes.value.toMutableList()
        if (index in currentTypes.indices) {
            currentTypes[index] = mode
            _counterTypes.value = currentTypes
            validateAutoAdvance()
        }
    }

    fun updateAutoAdvance(config: AutoAdvanceConfiguration) {
        _autoAdvance.value = config
        validateAutoAdvance()
    }

    fun updateIncrementSeconds(seconds: Int) { _incrementSeconds.value = seconds }
    fun updateStartingLife(life: Int) { _startingLife.value = life }
    fun updateStartingTimerSeconds(seconds: Int) { _startingTimerSeconds.value = seconds }

    // --- The Gatekeeper Logic ---
    private fun validateAutoAdvance() {
        val hasLifeCounter = _counterTypes.value.contains(CounterMode.LIFE)
        val currentConfig = _autoAdvance.value

        if (hasLifeCounter && currentConfig.enabled) {
            _autoAdvance.value = currentConfig.copy(enabled = false)
        }
    }

    fun savePlayset(onSaved: () -> Unit) {
        viewModelScope.launch {
            val typesString = _counterTypes.value.joinToString(",") { it.name }

            val playsetToSave = Playset(
                name = _name.value,
                playerCount = _playerCount.value,
                counterTypesJson = typesString,
                autoAdvance = _autoAdvance.value,
                incrementSeconds = _incrementSeconds.value,
                startingLife = _startingLife.value,
                startingTimerSeconds = _startingTimerSeconds.value,
                playsetID = editingPlaysetId
            )

            dao.insertPlayset(playsetToSave)
            onSaved()
        }
    }
}