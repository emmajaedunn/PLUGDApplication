package com.example.plugd.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.theme.Telegraf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutHelpPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title */ },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() // <-- Go back to ProfileScreen
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "THE PLUG ABOUT & HELP",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = Telegraf
                )
            )

            Text(
                text = "This is a placeholder for the about/support page.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}