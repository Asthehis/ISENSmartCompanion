package fr.isen.noemie.isensmartcompanion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.noemie.isensmartcompanion.api.EventApiService
import fr.isen.noemie.isensmartcompanion.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val apiService = EventApiService.create()
    private val eventRepository = EventRepository(apiService)

    private val _eventList = MutableStateFlow<List<Event>>(emptyList())
    val eventList: StateFlow<List<Event>> = _eventList
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            try {
                val events = eventRepository.fetchEvents().getOrThrow()
                Log.d("API", "Événements récupérés: $events")
                _eventList.value = events
            } catch (e: Exception) {
                Log.e("API", "Erreur lors de la récupération des événements", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}
