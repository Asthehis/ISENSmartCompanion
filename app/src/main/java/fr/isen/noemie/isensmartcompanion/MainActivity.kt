package fr.isen.noemie.isensmartcompanion

import GeminiAIService
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.foundation.lazy.items
import fr.isen.noemie.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel


// Define Tab Items
data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

val oswaldFontFamily = FontFamily(
    Font(R.font.oswald, FontWeight.Normal)
)

class MainActivity : ComponentActivity() {
    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                val navController = rememberNavController()

                // Create the tab items here
                val homeTab = TabBarItem(
                    title = "Home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home
                )
                val eventTab = TabBarItem(
                    title = "Event",
                    selectedIcon = Icons.Filled.DateRange,
                    unselectedIcon = Icons.Outlined.DateRange
                )
                val historyTab = TabBarItem(
                    title = "History",
                    selectedIcon = Icons.Filled.Refresh,
                    unselectedIcon = Icons.Outlined.Refresh
                )

                val tabBarItems = listOf(homeTab, eventTab, historyTab)

                // Observe eventList and isLoading from the ViewModel
                val eventList by eventViewModel.eventList.collectAsState()
                val isLoading by eventViewModel.isLoading.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { TabView(tabBarItems, navController) }
                ) { innerPadding ->
                    NavHost(navController, startDestination = homeTab.title, Modifier.padding(innerPadding)) {
                        composable(homeTab.title) { SmartCompanionUI() }
                        composable(eventTab.title) { EventView(eventList, isLoading, navController) }
                        composable(historyTab.title) { val messageViewModel: MessageViewModel = viewModel() // Récupère le ViewModel pour l'historique
                            HistoryScreen(viewModel = messageViewModel) }
                        composable("eventDetail/{eventId}") { backStackEntry ->
                            Log.d("EventDetail", "Raw eventId argument: ${backStackEntry.arguments?.getString("eventId")}")
                            val eventId = backStackEntry.arguments?.getString("eventId")
                            Log.d("EventDetail", "eventId récupéré: $eventId")

                            if (eventId == null) {
                                Log.e("EventDetail", "eventId est null, problème de navigation")
                                Text("Erreur de chargement")
                                return@composable
                            }

                            val selectedEvent = eventList.find { it.id == eventId }
                            if (selectedEvent != null) {
                                Log.d("EventDetail", "Événement trouvé: $selectedEvent")
                                EventDetailScreen(selectedEvent, onBackPress = { navController.popBackStack() })
                            } else {
                                Log.e("EventDetail", "Aucun événement trouvé avec l'ID: $eventId")
                                Text("Événement non trouvé")
                            }
                        }


                    }
                }
            }
        }
    }
}


// Reusable Tab View
@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    NavigationBar {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    Icon(
                        imageVector = if (selectedTabIndex == index) tabBarItem.selectedIcon else tabBarItem.unselectedIcon,
                        contentDescription = tabBarItem.title
                    )
                },
                label = { Text(tabBarItem.title) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colorResource(id = R.color.red)
            )
            )
        }
    }
}

// Home Screen
@Composable
fun SmartCompanionUI() {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: MessageViewModel = viewModel(factory = MessageViewModelFactory(context))
    var userInput by remember { mutableStateOf("") }
    val messages by viewModel.messagesInView.collectAsState(initial = emptyList()) // Utiliser collectAsState avec un Flow
    val aiService = remember { GeminiAIService(context) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "ISEN", fontSize = 96.sp, color = colorResource(id = R.color.red), fontFamily = oswaldFontFamily)
            Text(text = "Smart Companion", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ) {
                        items(messages) { message ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "You: ${message.userMessage}", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "AI: ${message.aiResponse}", fontSize = 18.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        label = { Text("Ask me anything...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        singleLine = true,
                        trailingIcon = {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val aiResponse = aiService.getAIResponse(userInput)
                                        viewModel.addMessage(userInput, aiResponse) // Sauvegarde dans Room
                                        userInput = "" // Réinitialise l'input après envoi
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.red)),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "Clear chat",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp).padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable {
                        viewModel.clearMessages() // Efface uniquement l'UI
                    }
            )
        }
    }
}

// Event Screen
@Composable
fun EventView(eventList: List<Event>, isLoading: Boolean, navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Events", fontSize = 36.sp)

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(eventList) { event ->
                        EventItem(event) {
                            navController.navigate("eventDetail/${event.id}")
                            Log.d("Navigation", "event.id envoyé: ${event.id}")
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SmartCompanionUIPreview() {
    ISENSmartCompanionTheme {
        SmartCompanionUI()
    }
}
