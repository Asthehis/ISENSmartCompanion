package fr.isen.noemie.isensmartcompanion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    // Mise à jour du champ isCleared pour tous les messages
    @Query("UPDATE messages SET isCleared = :isCleared")
    suspend fun updateMessagesClearStatus(isCleared: Boolean)

    // Requête pour récupérer seulement les messages non effacés
    @Query("SELECT * FROM messages WHERE isCleared = 0 ORDER BY timestamp DESC")
    fun getAllMessagesNotCleared(): Flow<List<MessageEntity>>
}