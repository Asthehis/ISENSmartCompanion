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
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import android.app.NotificationManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Demander la permission de notification si la version Android est >= 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            } else {
                Log.d("Permission", "Notification permission granted")
            }

        }

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

    // SharedPreferences pour stocker les notifications
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    val isNotified = remember { mutableStateOf(sharedPreferences.getBoolean(event.id, false)) }

    val coroutineScope = rememberCoroutineScope()

    // Function to trigger notification after a delay
    fun scheduleNotification() {
        if (isNotified.value) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = event.id.hashCode()

            // Delay of 10 seconds
            coroutineScope.launch {
                delay(10000) // 10 seconds
                val notification = NotificationCompat.Builder(context, "event_channel")
                    .setSmallIcon(R.drawable.ic_notification) // Assurez-vous d'avoir un icône
                    .setContentTitle("Rappel: ${event.title}")
                    .setContentText("N'oubliez pas de participer à l'événement : ${event.title}")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                // Envoyer la notification
                Log.d("Notification", "Notification planned for: ${event.title}")
                Log.d("Notification", "Sending notification...")

                notificationManager.notify(notificationId, notification)

            }
        }
    }

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

            Spacer(modifier = Modifier.height(16.dp))

            // Icone pour activer la notification
            IconButton(
                onClick = {
                    isNotified.value = !isNotified.value
                    sharedPreferences.edit().putBoolean(event.id, isNotified.value).apply()
                    Log.d("NotificationState", "Notification state: ${isNotified.value}") // Log l'état
                    if (isNotified.value) {
                        Toast.makeText(context, "Notification activée pour cet événement", Toast.LENGTH_SHORT).show()
                        scheduleNotification()  // Planifie la notification
                    } else {
                        Toast.makeText(context, "Notification désactivée pour cet événement", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(
                    imageVector = if (isNotified.value) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                    contentDescription = "Toggle Notification"
                )
            }

        }
    }
}
