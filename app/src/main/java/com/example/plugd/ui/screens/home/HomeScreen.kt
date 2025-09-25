package com.example.plugd.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.data.EventEntity
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.viewmodels.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    role: String,
    eventViewModel: EventViewModel
) {
    val scope = rememberCoroutineScope()
    val events by eventViewModel.relevantEvents.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(TimeFilter.ALL) }

    val filtered = remember(events, searchQuery, selectedFilter) {
        events.filter { ev ->
            val matchesQuery = searchQuery.isBlank() ||
                    ev.name.contains(searchQuery, ignoreCase = true) ||
                    ev.createdBy.contains(searchQuery, ignoreCase = true)
            val matchesTime = when (selectedFilter) {
                TimeFilter.ALL -> true
                TimeFilter.TODAY -> isSameDay(ev.date)
                TimeFilter.THIS_WEEK -> isInThisWeek(ev.date)
            }
            matchesQuery && matchesTime
        }
    }

    LaunchedEffect(Unit) { eventViewModel.syncEventsToLocal() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover") },
                actions = {
                    IconButton(onClick = { scope.launch { eventViewModel.syncEventsToLocal() } }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            if (role == "Artist" || role == "EventVenue") {
                FloatingActionButton(onClick = { navController.navigate(Routes.ADD) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- Search Bar ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search events...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            // --- Time Filters ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("All", selectedFilter == TimeFilter.ALL) { selectedFilter = TimeFilter.ALL }
                FilterChip("Today", selectedFilter == TimeFilter.TODAY) { selectedFilter = TimeFilter.TODAY }
                FilterChip("This Week", selectedFilter == TimeFilter.THIS_WEEK) { selectedFilter = TimeFilter.THIS_WEEK }
            }

            Spacer(Modifier.height(12.dp))

            // --- Events List ---
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(filtered) { ev ->
                    EventListItem(
                        event = ev,
                        onClick = { navController.navigate("event_detail/${ev.id}") },
                        onJoin = { eventViewModel.joinEvent(ev) }
                    )
                    Divider()
                }
            }
        }
    }
}

private fun EventViewModel.joinEvent(ev: EventEntity) {}

// --- Helpers ---
@Composable
fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun EventListItem(event: EventEntity, onClick: () -> Unit, onJoin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }
    ) {
        Text(text = event.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        val dateText = event.date?.let { fmtDate(it) } ?: "TBA"
        Text(text = "$dateText • ${event.location ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onJoin) { Text("Join") }
        }
    }
}

enum class TimeFilter { ALL, TODAY, THIS_WEEK }

fun isSameDay(timestamp: Long?): Boolean {
    if (timestamp == null) return false
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val now = Calendar.getInstance()
    return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
}

fun isInThisWeek(timestamp: Long?): Boolean {
    if (timestamp == null) return false
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val now = Calendar.getInstance()
    return cal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR) &&
            cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
}

fun fmtDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM • HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}