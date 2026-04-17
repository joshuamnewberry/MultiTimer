package edu.gvsu.cis.multi_timer.viewModels

import androidx.lifecycle.ViewModel
import edu.gvsu.cis.multi_timer.data.AppDAO
import edu.gvsu.cis.multi_timer.data.Playset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(val dao: AppDAO): ViewModel() {
    private val _currentPlayset = MutableStateFlow<Playset>(Playset())
    val currentPlayset: StateFlow<Playset> = _currentPlayset.asStateFlow()
}