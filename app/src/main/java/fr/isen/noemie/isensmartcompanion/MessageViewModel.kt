package fr.isen.noemie.isensmartcompanion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MessageDatabase.getDatabase(application).messageDao()
    val messages: Flow<List<MessageEntity>> = db.getAllMessages()

    // Liste des messages visibles gérée par State
    var messagesInView by mutableStateOf<List<MessageEntity>>(emptyList())
        private set

    fun addMessage(userInput: String, aiResponse: String) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val newMessage = MessageEntity(userMessage = userInput, aiResponse = aiResponse, timestamp = timestamp)
            db.insertMessage(newMessage)

            // Mise à jour des messages visibles dans l'UI
            messagesInView = messagesInView + newMessage
            Log.d("MessageViewModel", "Message ajouté : $newMessage")
        }
    }

    // Effacer tous les messages visibles dans l'UI
    fun clearMessages() {
        messagesInView = emptyList() // Vider les messages visibles
        Log.d("MessageViewModel", "Messages effacés de l'UI : $messagesInView")
    }

    fun deleteMessage(id: String) {
        viewModelScope.launch {
            db.deleteMessage(id)
        }
    }

    fun deleteAllMessages() {
        viewModelScope.launch {
            db.deleteAllMessages()
        }
    }
}
