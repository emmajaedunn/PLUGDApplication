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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.screens.home.SegmentedButtonGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFilterScreen(navController: NavHostController) {
    var category by remember { mutableStateOf("") }

    // true = Newest → Oldest, false = Oldest → Newest
    var sortByLatest by remember { mutableStateOf(true) }

    // NEW categories
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

            // CATEGORY DROPDOWN
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = if (category.isBlank()) "Select Category" else category,
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

            // SORT BY TOGGLE
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

            // APPLY FILTERS BUTTON
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

            // CLEAR FILTERS BUTTON
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

/*@Composable
fun SegmentedButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row {
        options.forEachIndexed { index, option ->
            Button(
                onClick = { onOptionSelected(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (option == selectedOption)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.LightGray
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(option, fontSize = 12.sp)
            }

            if (index != options.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}*/
