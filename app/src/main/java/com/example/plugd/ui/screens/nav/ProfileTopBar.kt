package com.example.plugd.ui.screens.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavHostController) {
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
                navController.navigate("settings")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.btn_settings),
                    contentDescription = "Settings"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Black
        )
    )
}