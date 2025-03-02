package fr.isen.noemie.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.noemie.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SmartCompanionUI(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SmartCompanionUI(modifier: Modifier = Modifier) {
    var userInput by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Hello! How can I assist you?") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo and Title
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(160.dp)
        )
        Text(text = "Smart Companion", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // Input Field
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Ask me anything...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Send Button
        Button(
            onClick = { response = "This is a test response from the assistant." },
            shape = CircleShape,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Send")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // AI Response
        Text(
            text = response,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SmartCompanionUIPreview() {
    ISENSmartCompanionTheme {
        SmartCompanionUI()
    }
}
