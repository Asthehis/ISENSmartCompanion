package fr.isen.noemie.isensmartcompanion

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.Serializable

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String
) : Serializable


// Fake Events List
val fakeEvents = listOf(
    Event(1, "BDE Evening", "A fun night organized by the student association.", "2025-03-10", "ISEN Campus", "Party"),
    Event(2, "Gala", "The annual ISEN Gala with dinner and music.", "2025-05-15", "Grand Hotel", "Formal"),
    Event(3, "Cohesion Day", "A day of team-building activities.", "2025-09-05", "Outdoor Park", "Social"),
    Event(4, "Tech Conference", "Talks and networking with tech leaders.", "2025-06-20", "ISEN Auditorium", "Conference")
)

class EventListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventListScreen()
        }
    }
}

@Composable
fun EventListScreen() {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "ISEN Events", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn {
            items(fakeEvents) { event ->
                EventItem(event) {
                    val intent = Intent(context, EventDetailActivity::class.java).apply {
                        putExtra("event", event) // Pas besoin de modification ici
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontSize = 20.sp, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${event.date} - ${event.location}", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.description, fontSize = 14.sp)
        }
    }
}

@Deprecated("This activity will be refactored, use another approach")
class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération de l'objet Event via getSerializableExtra et casting en Event
        val event = intent.getSerializableExtra("event") as? Event

        setContent {
            event?.let { EventDetailScreen(it) }
        }
    }
}

@Composable
fun EventDetailScreen(event: Event) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = event.title, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Date: ${event.date}")
        Text(text = "Location: ${event.location}")
        Text(text = "Category: ${event.category}")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = event.description)
    }
}
