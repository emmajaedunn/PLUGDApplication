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

    // Local filter state (coming from FilterScreen)
    var filterLocation by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf("") }
    var sortByLatest by remember { mutableStateOf(true) }

    // Listen for values sent by FilterScreen
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

    // Apply search + filters + sort
    val filteredEvents = remember(
        events,
        searchQuery,
        filterLocation,
        filterCategory,
        sortByLatest
    ) {
        events
            .asSequence()
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