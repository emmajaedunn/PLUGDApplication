package com.example.plugd.ui.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.screens.home.SegmentedButtonGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFilterScreen(navController: NavHostController) {
    var category by remember { mutableStateOf("") }

    // Sort by newest by default
    var sortByLatest by remember { mutableStateOf(true) }

    // Categories
    val categories = listOf(
        "Friend requests",
        "Friend activity",
        "Community activity"
    )

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

            // Category Dropdown
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = category.ifBlank { "Select Category" },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
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
                        .width(250.dp),
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

            // Sort by toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sort by: ")
                Spacer(modifier = Modifier.width(8.dp))

                SegmentedButtonGroup(
                    options = listOf("Newest", "Oldest"),
                    selectedOption = if (sortByLatest) "Newest" else "Oldest",
                    onOptionSelected = { selected ->
                        sortByLatest = selected == "Newest"
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Apply filters button
            Button(
                onClick = {
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

            // Clear filters button
            OutlinedButton(
                onClick = {
                    category = ""
                    sortByLatest = true

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