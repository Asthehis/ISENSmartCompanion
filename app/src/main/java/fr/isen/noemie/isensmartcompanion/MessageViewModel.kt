package fr.isen.noemie.isensmartcompanion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MessageDatabase.getDatabase(application).messageDao()
    val messages: Flow<List<MessageEntity>> = db.getAllMessages()

    fun addMessage(userInput: String, aiResponse: String) {
        viewModelScope.launch {
            db.insertMessage(MessageEntity(userMessage = userInput, aiResponse = aiResponse))
        }
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
