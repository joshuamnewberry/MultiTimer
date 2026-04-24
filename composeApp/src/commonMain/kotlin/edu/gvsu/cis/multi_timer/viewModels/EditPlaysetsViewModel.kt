package edu.gvsu.cis.multi_timer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.multi_timer.data.AppDAO
import edu.gvsu.cis.multi_timer.data.AutoAdvanceConfiguration
import edu.gvsu.cis.multi_timer.data.CounterMode
import edu.gvsu.cis.multi_timer.data.Playset
import edu.gvsu.cis.multi_timer.PlaysetSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPlaysetsViewModel(
    private val dao: AppDAO,
    private val sessionManager: PlaysetSessionManager
) : ViewModel() {

    // The temporary playset being built or edited
    private val _draftPlayset = MutableStateFlow(Playset())
    val draftPlayset = _draftPlayset.asStateFlow()

    private val _counterTypes = MutableStateFlow<List<CounterMode>>(
        List(2) { CounterMode.TIMER }
    )
    val counterTypes = _counterTypes.asStateFlow()

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
                _draftPlayset.value = playset

                // Deserialize the comma-separated string back into a list of Enums
                val modes = playset.counterTypesJson.split(",").mapNotNull {
                    try { CounterMode.valueOf(it) } catch (e: Exception) { null }
                }

                // Ensure the list perfectly matches the player count
                _counterTypes.value = if (modes.size == playset.playerCount) {
                    modes
                } else {
                    List(playset.playerCount) { CounterMode.TIMER }
                }

                validateAutoAdvance()
            }
        }
    }

    fun updateName(newName: String) {
        _draftPlayset.value = _draftPlayset.value.copy(name = newName)
    }

    fun updatePlayerCount(count: Int) {
        _draftPlayset.value = _draftPlayset.value.copy(playerCount = count)

        // Grow or shrink the counterTypes list to match the new player count
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
        _draftPlayset.value = _draftPlayset.value.copy(autoAdvance = config)
        validateAutoAdvance()
    }

    // --- The Gatekeeper Logic ---
    private fun validateAutoAdvance() {
        val hasLifeCounter = _counterTypes.value.contains(CounterMode.LIFE)
        val currentAutoAdvance = _draftPlayset.value.autoAdvance

        if (hasLifeCounter && currentAutoAdvance.enabled) {
            // Force disable auto-advance at the data level.
            // The UI will automatically observe this state change and toggle the switch off.
            _draftPlayset.value = _draftPlayset.value.copy(
                autoAdvance = currentAutoAdvance.copy(enabled = false)
            )
        }
    }

    fun savePlayset(onSaved: () -> Unit) {
        viewModelScope.launch {
            // Serialize the list of modes into a simple comma-separated string
            val typesString = _counterTypes.value.joinToString(",") { it.name }

            val finalPlayset = _draftPlayset.value.copy(
                counterTypesJson = typesString
            )

            // Insert will replace if the primary key (playsetID) already exists,
            // otherwise it will create a brand new one.
            dao.insertPlayset(finalPlayset)
            onSaved()
        }
    }
}