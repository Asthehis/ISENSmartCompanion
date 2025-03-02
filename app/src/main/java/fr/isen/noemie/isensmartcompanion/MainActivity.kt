package fr.isen.noemie.isensmartcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import fr.isen.noemie.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

// Define Tab Items
data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                val navController = rememberNavController()

                // Define TabBar Items
                val homeTab = TabBarItem(title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
                val eventTab = TabBarItem(title = "Event", selectedIcon = Icons.Filled.DateRange, unselectedIcon = Icons.Outlined.DateRange)
                val historyTab = TabBarItem(title = "History", selectedIcon = Icons.Filled.Refresh, unselectedIcon = Icons.Outlined.Refresh)

                val tabBarItems = listOf(homeTab, eventTab, historyTab)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { TabView(tabBarItems, navController) }
                ) { innerPadding ->
                    NavHost(navController, startDestination = homeTab.title, Modifier.padding(innerPadding)) {
                        composable(homeTab.title) { SmartCompanionUI() }
                        composable(eventTab.title) { EventView() }
                        composable(historyTab.title) { HistoryView() }
                    }
                }
            }
        }
    }
}

// Reusable Tab View
@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

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
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier.size(160.dp)
        )
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
fun EventView() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Events", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                context.startActivity(Intent(context, EventDetailActivity::class.java))
            }
        ) {
            Text("View Event Details")
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
