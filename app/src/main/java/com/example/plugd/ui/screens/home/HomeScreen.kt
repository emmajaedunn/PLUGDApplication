package com.example.plugd.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.HomeTopBar
import com.example.plugd.ui.screens.theme.LightOrange
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.viewmodels.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    userId: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    val events by eventViewModel.events.collectAsState(initial = emptyList())

    // Filter events based on search query (category)

    // Local filter state (coming from FilterScreen)
    var filterLocation by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf("") }
    var sortByLatest by remember { mutableStateOf(true) }

    // 🔹 Listen for values sent by FilterScreen
    val backStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(backStackEntry) {
        backStackEntry?.savedStateHandle?.get<String>("filter_location")?.let {
            filterLocation = it
        }
        backStackEntry?.savedStateHandle?.get<String>("filter_category")?.let {
            filterCategory = it
        }
        backStackEntry?.savedStateHandle?.get<Boolean>("filter_sort_latest")?.let {
            sortByLatest = it
        }
    }

    LaunchedEffect(Unit) {
        eventViewModel.loadEvents()
    }

    // 🔹 Apply search + filters + sort
    val filteredEvents = remember(
        events,
        searchQuery,
        filterLocation,
        filterCategory,
        sortByLatest
    ) {
        events
            .asSequence()
            // search text (your existing logic)
            .filter { event ->
                if (searchQuery.isBlank()) true else {
                    event.name.contains(searchQuery, ignoreCase = true) ||
                            event.description.contains(searchQuery, ignoreCase = true) ||
                            event.location.contains(searchQuery, ignoreCase = true) ||
                            event.createdByName.contains(searchQuery, ignoreCase = true)
                }
            }
            // location filter
            .filter { event ->
                filterLocation.isBlank() ||
                        event.location.contains(filterLocation, ignoreCase = true)
            }
            // category filter
            .filter { event ->
                filterCategory.isBlank() ||
                        event.category.equals(filterCategory, ignoreCase = true)
            }
            // sort (replace `event.timestamp` with your actual field)
            .let { seq ->
                if (sortByLatest) seq.sortedByDescending { it.date}
                else seq.sortedBy { it.date }
            }
            .toList()
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange
            )
        },
        bottomBar = {
            // Bottom nav specific for this screen
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
            }
        },
    ) { padding ->
        // --- CHANGE: Replaced the inefficient Column with a performant LazyColumn ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "PLUG IN TO THE LATEST",
                    style = MaterialTheme.typography.displayLarge.copy(fontFamily = Telegraf)
                )
            }

            if (filteredEvents.isEmpty()) {
                item {
                    Text(
                        "No plugs found.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(filteredEvents) { event ->
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








        /*padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PLUG IN TO THE LATEST",
                style = MaterialTheme.typography.displayLarge.copy(fontFamily = Telegraf)
            )

            if (filteredEvents.isEmpty()) {
                Text(
                    "No plugs found.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                filteredEvents.forEach { event ->
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
}*/















/*package com.example.plugd.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.HomeTopBar
import com.example.plugd.ui.screens.theme.LightOrange
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.viewmodels.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    //userId: String
) {
    val events by eventViewModel.events.collectAsState(initial = emptyList())

    // JUST ADDED search query
    var searchQuery by remember { mutableStateOf("") }

    //Load events when screen opens
    LaunchedEffect(Unit) {
        eventViewModel.loadEvents()
    }

    Scaffold(
        topBar = { HomeTopBar(
            navController = navController,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        ) }
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
        colors = CardDefaults.cardColors(containerColor = LightOrange)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.name, style = MaterialTheme.typography.titleLarge)
            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "📍 ${event.location}", style = MaterialTheme.typography.bodySmall)
            Text(text = "🗓 $eventDate", style = MaterialTheme.typography.bodySmall)
            Text(text = "Created by: ${event.createdByName}", style = MaterialTheme.typography.bodySmall)
        }
    }
}*/