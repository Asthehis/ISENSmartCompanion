package fr.isen.noemie.isensmartcompanion

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MessageDatabase.getDatabase(application).messageDao()
    private val context = application.applicationContext
    private val sharedPreferences = context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE)

    private val _messagesInView = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messagesInView: StateFlow<List<MessageEntity>> = _messagesInView // expose le Flow

    private val _isMessagesCleared = MutableStateFlow(false)
    val isMessagesCleared: StateFlow<Boolean> = _isMessagesCleared

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            db.getAllMessages().collect { storedMessages ->
                _messagesInView.value = storedMessages
            }
        }
    }

    fun addMessage(userInput: String, aiResponse: String) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val newMessage = MessageEntity(userMessage = userInput, aiResponse = aiResponse, timestamp = timestamp)
            db.insertMessage(newMessage)
            _messagesInView.value = _messagesInView.value + newMessage
        }
    }

    fun clearMessages() {
        viewModelScope.launch {
            db.updateMessagesClearStatus(true)
            sharedPreferences.edit().putBoolean("isMessagesCleared", true).apply()
            _isMessagesCleared.value = true
            _messagesInView.value = emptyList()
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
