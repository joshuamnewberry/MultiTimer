package edu.gvsu.cis.multi_timer.viewModels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.multi_timer.sessionManager
import edu.gvsu.cis.multi_timer.data.AppDAO
import edu.gvsu.cis.multi_timer.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPlayersViewModel(
    private val dao: AppDAO,
    sessionManager: sessionManager
) : ViewModel() {
    private var editingPlayerId: Int = 0

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _profilePicture = MutableStateFlow("")
    val profilePicture = _profilePicture.asStateFlow()

    private val _redColor = MutableStateFlow(0f)
    val redColor = _redColor.asStateFlow()

    private val _greenColor = MutableStateFlow(0f)
    val greenColor = _greenColor.asStateFlow()

    private val _blueColor = MutableStateFlow(0f)
    val blueColor = _blueColor.asStateFlow()

    init {
        val idToEdit = sessionManager.currentEditId
        if (idToEdit != null) {
            loadPlayerById(idToEdit)
            sessionManager.currentEditId = null
        } else {
            // Default new players to White
            unpackColors(0xFFFFFFFF)
        }
    }

    private fun loadPlayerById(id: Int) {
        viewModelScope.launch {
            val player = dao.getPlayerById(id)
            if (player != null) {
                editingPlayerId = player.playerID
                _name.value = player.name
                _profilePicture.value = player.profilePicture ?: ""
                unpackColors(player.playerBackgroundColor)
            }
        }
    }

    private fun unpackColors(colorValue: Long) {
        val temp = Color(colorValue)
        _redColor.value = temp.red
        _greenColor.value = temp.green
        _blueColor.value = temp.blue
    }

    private fun packColors(): Long {
        return Color(
            red = _redColor.value,
            green = _greenColor.value,
            blue = _blueColor.value,
            alpha = 1f
        ).toArgb().toLong()
    }

    // Updates from user
    fun updateName(newName: String) { _name.value = newName }
    fun updateProfilePicture(newPic: String) { _profilePicture.value = newPic }
    fun updateRed(red: Float) { _redColor.value = red }
    fun updateGreen(green: Float) { _greenColor.value = green }
    fun updateBlue(blue: Float) { _blueColor.value = blue }

    fun savePlayer(onSaved: () -> Unit) {
        viewModelScope.launch {
            dao.insertPlayer(
                Player(
                    name = _name.value,
                    profilePicture = _profilePicture.value.ifEmpty { null },
                    playerBackgroundColor = packColors(),
                    playerID = editingPlayerId
                )
            )
            onSaved()
        }
    }
}