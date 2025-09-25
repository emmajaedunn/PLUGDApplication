package com.example.plugd.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.ui.screens.nav.ProfileTopBar

@Composable
fun ProfileScreen(navController: NavHostController) {
    Scaffold(
        topBar = { ProfileTopBar(navController = navController)
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
                text = "Profile",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "This is a placeholder for the profile page.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}