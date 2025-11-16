package com.example.plugd.ui.screens.nav

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    navController: NavHostController,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search for PLUGS") },
                singleLine = true,
                shape = RoundedCornerShape(50),
                textStyle = TextStyle(fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = Color.Black,
                    focusedBorderColor = Color.Black,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.plugd_icon),
                contentDescription = "PLUGD Logo",
                modifier = Modifier
                    .size(60.dp)
                    .padding(start = 16.dp)
            )
        },actions = {
            IconButton(onClick = { navController.navigate("home_filter")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.btn_filter),
                    contentDescription = "Filter"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Black
        )
    )
}



















/*package com.example.plugd.ui.screens.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavHostController) {
    TopAppBar(
        title = { /* No title */ },
        navigationIcon = {
            // Logo on the left
            Icon(
                painter = painterResource(id = R.drawable.plugd_icon),
                contentDescription = "PLUGD Logo",
                modifier = Modifier
                    .size(60.dp)
                    .padding(start = 16.dp)
            )
        },
        actions = {
            // Settings button on the right
            IconButton(onClick = {
                navController.navigate("filter")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.btn_filter),
                    contentDescription = "Filter"
                )
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Black
        )
    )
}*/

