package fr.isen.noemie.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.noemie.isensmartcompanion.Event
import fr.isen.noemie.isensmartcompanion.EventItem
import android.util.Log

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val event = intent.getSerializableExtra("event") as? Event

        setContent {
            event?.let {
                EventDetailScreen(it, onBackPress = { finish() })
            } ?: run {
                Text("Erreur : Aucun événement trouvé", fontSize = 20.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(event: Event, onBackPress: () -> Unit) {
    Log.d("EventDetailScreen", "Affichage de l'événement: $event")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails de l'événement") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = event.title, fontSize = 24.sp, style = MaterialTheme.typography.headlineMedium)
            Text(text = "📅 Date : ${event.date}", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
            Text(text = "📍 Lieu : ${event.location}", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
            Text(text = "🔖 Catégorie : ${event.category}", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Description", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Text(text = event.description, fontSize = 16.sp, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
