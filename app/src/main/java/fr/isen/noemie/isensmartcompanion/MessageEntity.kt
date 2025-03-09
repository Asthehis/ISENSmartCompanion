package fr.isen.noemie.isensmartcompanion

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(), // Génère un ID unique
    val userMessage: String,
    val aiResponse: String,
    val timestamp: Long = System.currentTimeMillis() // Ajoute la date
)
