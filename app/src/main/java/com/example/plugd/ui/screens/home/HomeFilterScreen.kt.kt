package com.example.plugd.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFilterScreen(navController: NavHostController) {
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var sortByLatest by remember { mutableStateOf(true) }

    val categories = listOf("Music", "Event", "Collab", "General", "Venue")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filters") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Location Filter
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location", fontWeight = FontWeight.Normal, fontSize = 17.sp) },
                modifier = Modifier.fillMaxWidth()
            )

            // Category Dropdown
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = category.ifEmpty { "Select Category" },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        }
                    }
                )
                DropdownMenu(
                    modifier = Modifier
                        .background(Color(0xFFFFF3E0))
                        .width(150.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Sort By Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sort by: ")
                Spacer(modifier = Modifier.width(8.dp))
                SegmentedButtonGroup(
                    options = listOf("Latest", "Oldest"),
                    selectedOption = if (sortByLatest) "Latest" else "Oldest",
                    onOptionSelected = { sortByLatest = it == "Latest" }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Apply Filters Button
            Button(
                onClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("filter_location", location)

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("filter_category", category)

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("filter_sort_latest", sortByLatest)

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters", fontWeight = FontWeight.SemiBold)
            }

            // Clear Filters Button
            OutlinedButton(
                onClick = {
                    // reset local state
                    location = ""
                    category = ""
                    sortByLatest = true

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("filter_location", "")

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("filter_category", "")

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("filter_sort_latest", true)

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Clear Filters", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SegmentedButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row {
        options.forEach { option ->
            Button(
                onClick = { onOptionSelected(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (option == selectedOption) MaterialTheme.colorScheme.primary else Color.LightGray
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(option)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}