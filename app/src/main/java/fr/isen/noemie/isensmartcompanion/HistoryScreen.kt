package fr.isen.noemie.isensmartcompanion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: MessageViewModel) {
    val messages by viewModel.messagesInView.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Titre "History" centré en haut
        Box(
            modifier = Modifier
                .fillMaxWidth() // S'assurer que le Box prend toute la largeur
                .padding(16.dp)
        ) {
            Text(
                text = "History",
                fontSize = 36.sp,
                modifier = Modifier.align(Alignment.Center) // Centrer le texte à l'intérieur du Box
            )
        }

        // Espace entre le titre et les messages
        Spacer(modifier = Modifier.height(16.dp))

        // Affichage des messages
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Donne de l'espace pour le contenu avant le bouton
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Affichage des messages
                    Text(text = "You: ${message.userMessage}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "AI: ${message.aiResponse}", fontSize = 18.sp)

                    // Affichage de la date
                    val date = Date(message.timestamp)
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
                    Text(text = "Date: $formattedDate", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bouton pour supprimer un message spécifique
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            viewModel.deleteMessage(message.id)
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Message")
                        }
                    }
                }
                Divider()
            }
        }

        // Espace entre les messages et le bouton "Clear history"
        Spacer(modifier = Modifier.height(16.dp))

        // Bouton "Clear history" en bas
        Button(
            onClick = {
                viewModel.deleteAllMessages() // Effacer tous les messages de la base de données et de l'UI
            },
            modifier = Modifier
                .fillMaxWidth() // Prendre toute la largeur disponible
                .padding(16.dp), // Espacement autour du bouton
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.red))
        ) {
            Text(
                text = "Clear history",
                color = Color.White, // Texte en blanc
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
