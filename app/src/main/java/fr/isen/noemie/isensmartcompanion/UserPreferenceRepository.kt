package fr.isen.noemie.isensmartcompanion

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    private val CHAT_CLEARED_KEY = booleanPreferencesKey("chat_cleared")

    val isChatCleared: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[CHAT_CLEARED_KEY] ?: false }

    suspend fun setChatCleared(isCleared: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CHAT_CLEARED_KEY] = isCleared
        }
    }
}
