package com.example.plugd.ui.screens.plug

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.AddTopBar
import com.example.plugd.viewmodels.EventViewModel
import kotlinx.coroutines.launch
import android.Manifest
import android.net.Uri
import android.widget.Toast

@Composable
fun AddPlugScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    currentUserId: String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Remember input state
    var pluggingWhat by remember { mutableStateOf("") }
    var plugCategory by remember { mutableStateOf("") }
    var plugTitle by remember { mutableStateOf("") }
    var plugDescription by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var supportDocs by remember { mutableStateOf<String?>(null) }

    // Launcher to pick a file
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->  // Explicitly specify the type
        uri?.let {
            supportDocs = it.toString()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            filePickerLauncher.launch("*/*")
        } else {
            Toast.makeText(context, "Permission denied to access files", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { AddTopBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Text(
                text = "Create a Plug",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = "Submit an application for services, jobs, gig opportunities & collaborative opportunities.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            // Input Fields
            InputField(
                label = "What are you plugging?",
                value = pluggingWhat,
                onValueChange = { pluggingWhat = it },
                placeholder = "ex. Looking for an artist for a gig on Friday."
            )
            InputField(
                label = "Plug Category",
                value = plugCategory,
                onValueChange = { plugCategory = it },
                placeholder = "ex. Gig Opportunity"
            )
            InputField(
                label = "Plug Title",
                value = plugTitle,
                onValueChange = { plugTitle = it },
                placeholder = "ex. Artist wanted"
            )
            InputField(
                label = "Plug Description",
                value = plugDescription,
                onValueChange = { plugDescription = it },
                placeholder = "ex. Date: 10/07/2025 | Time: 6-8PM | Venue: Openwine"
            )
            InputField(
                label = "Location",
                value = location,
                onValueChange = { location = it },
                placeholder = "ex. 31 Bree Street Cape Town"
            )

            // File picker for support docs
            FilePickerField(supportDocs = supportDocs) { uri ->
                supportDocs = uri.toString()
            }

            Spacer(Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    val newEvent = EventEntity(
                        eventId = System.currentTimeMillis().toString(),
                        name = plugTitle,
                        category = plugCategory,
                        description = plugDescription,
                        location = location,
                        date = System.currentTimeMillis(),
                        createdBy = currentUserId,
                        supportDocs = supportDocs?.takeIf { it.isNotEmpty() }
                    )
                    scope.launch {
                        try {
                            eventViewModel.addEvent(newEvent)
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace() // log the error to see why it crashes
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}

// --- Reusable Input Field ---
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String = "") {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}