package fr.isen.noemie.isensmartcompanion

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState


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
                        composable(historyTab.title) { HistoryView() }
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
                label = { Text(tabBarItem.title) }
            )
        }
    }
}

// Home Screen
@Composable
fun SmartCompanionUI(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf("") }
    var previousInput by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Hello! How can I assist you?") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text= "ISEN", fontSize = 96.sp, color = colorResource(id = R.color.red), fontFamily = oswaldFontFamily)
        Text(text = "Smart Companion", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Ask me anything...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                response = "Previous request: $previousInput\nYour request: $userInput"
                previousInput = userInput
                Toast.makeText(context, "Question Submitted", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.red))
        ) {
            Text("Send")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = response,
            fontSize = 18.sp,
            color = colorResource(id = R.color.grey)
        )
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
            Text(text = "Events", fontSize = 24.sp)

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

// History Screen
@Composable
fun HistoryView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "History", fontSize = 24.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun SmartCompanionUIPreview() {
    ISENSmartCompanionTheme {
        SmartCompanionUI()
    }
}
