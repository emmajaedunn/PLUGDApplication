package com.example.plugd.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plugd.viewmodels.EventViewModel
import com.example.plugd.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    eventId: String,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    eventViewModel: EventViewModel // Kept for consistency, but profileViewModel is used for logic
) {
    // The source of truth for a user's own events is the ProfileViewModel
    val userEvents by profileViewModel.userEvents.collectAsState()
    val event = remember(userEvents, eventId) {
        userEvents.find { it.eventId == eventId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit PLUG") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Handle the case where the event is not (yet) found
        if (event == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Event not found or still loading...")
            }
        } else {
            // State for the editable text fields, initialized with the event's data
            var title by remember { mutableStateOf(event.name) }
            var description by remember { mutableStateOf(event.description) }
            var location by remember { mutableStateOf(event.location) }

            // This Column holds the main content of the screen
            Column(modifier = Modifier.padding(innerPadding)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // This Row contains the fully corrected buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // "Save Changes" button
                            Button(
                                onClick = {
                                    // CORRECTED: Use profileViewModel to update the event
                                    eventViewModel.updateEvent(
                                        event.copy(name = title, description = description, location = location)
                                    )
                                    navController.popBackStack()
                                },
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text("Save Changes", fontWeight = FontWeight.W600)
                            }

                            // "Delete" button
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                onClick = {
                                    // CORRECTED: Use profileViewModel to delete the event
                                    eventViewModel.deleteEvent(event.eventId)
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Delete", color = Color.White, fontWeight = FontWeight.W600)
                            }
                        }
                    }
                }
            }
        }
    }
}













/*package com.example.plugd.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.plugd.viewmodels.ProfileViewModel

@Composable
fun EditEventScreen(
    eventId: String,
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val userEvents by profileViewModel.userEvents.collectAsState()
    val event = userEvents.find { it.eventId.toString() == eventId }

    if (event == null) {
        Text("Event not found")
        return
    }

    var title by remember { mutableStateOf(event.name) }
    var description by remember { mutableStateOf(event.description) }
    var location by remember { mutableStateOf(event.location) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                // Update via API
                profileViewModel.updateEvent(
                    event.copy(name = title, description = description, location = location)
                )
                navController.popBackStack()
            }) {
                Text("Save Changes")
            }

            Button(
                colors = ButtonDefaults.buttonColors(contentColor = Color.Red),
                onClick = {
                    profileViewModel.deleteEvent(event.eventId) // delete via API
                    navController.popBackStack()
                }
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }
}*/