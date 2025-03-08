package fr.isen.noemie.isensmartcompanion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.noemie.isensmartcompanion.api.EventApiService
import fr.isen.noemie.isensmartcompanion.Event
import fr.isen.noemie.isensmartcompanion.EventItem
import fr.isen.noemie.isensmartcompanion.repository.EventRepository
import java.io.Serializable
import androidx.activity.viewModels

class EventListActivity : ComponentActivity() {
    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val eventList = eventViewModel.eventList.collectAsState()
            val isLoading = eventViewModel.isLoading.collectAsState()

            EventListScreen(eventList.value, isLoading.value)
        }
    }
}

@Composable
fun EventListScreen(eventList: List<Event>, isLoading: Boolean) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "ISEN Events",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(eventList) { event ->
                    EventItem(event) {
                        // Handle navigation or any other event
                    }
                }
            }
        }
    }
}