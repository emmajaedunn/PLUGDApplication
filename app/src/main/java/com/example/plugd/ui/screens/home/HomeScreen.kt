package com.example.plugd.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.HomeTopBar
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.viewmodels.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel
) {
    val events by eventViewModel.events.collectAsState(initial = emptyList())

    // Load events when screen opens
    LaunchedEffect(Unit) {
        eventViewModel.loadEvents()
    }

    Scaffold(
        topBar = { HomeTopBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PLUG IN TO THE LATEST",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = Telegraf
                )
            )

            if (events.isEmpty()) {
                Text(
                    text = "No plugs yet. Be the first to add one!",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(events) { event ->
                        EventCard(
                            event = event,
                            modifier = Modifier.clickable {
                                navController.navigate("${Routes.PLUG_DETAILS}/${event.eventId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- Reusable EventCard Composable ---
@Composable
fun EventCard(event: EventEntity, modifier: Modifier = Modifier) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val eventDate = dateFormat.format(Date(event.date))

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.name, style = MaterialTheme.typography.titleLarge)
            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "📍 ${event.location}", style = MaterialTheme.typography.bodySmall)
            Text(text = "🗓 $eventDate", style = MaterialTheme.typography.bodySmall)
            Text(text = "Created by: ${event.createdBy}", style = MaterialTheme.typography.bodySmall)
        }
    }
}