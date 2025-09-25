package com.example.plugd.ui.screens.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onMenuClick: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo
                Icon(
                    painter = painterResource(id = R.drawable.plugd_icon),
                    contentDescription = "PLUGD",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search event, artist, etc") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )

                // Menu / Filter button
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        }
    )
}