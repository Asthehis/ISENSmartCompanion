package fr.isen.noemie.isensmartcompanion.repository

import fr.isen.noemie.isensmartcompanion.Event
import fr.isen.noemie.isensmartcompanion.api.EventApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class EventRepository(private val apiService: EventApiService) {
    suspend fun fetchEvents(): Result<List<Event>> {
        return withContext(Dispatchers.IO) {
            try {
                val events = apiService.getEvents()
                Result.success(events)
            } catch (e: IOException) {
                Result.failure(e) // Problème de connexion
            } catch (e: Exception) {
                Result.failure(e) // Autre erreur
            }
        }
    }
}