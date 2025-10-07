// Implemented in Final POE
package com.example.plugd.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricSettingsPage(navController: NavHostController) {
    var resultMessage by remember { mutableStateOf("Not authenticated yet") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biometric Login") },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.btn_back),
                        contentDescription = "Back"
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(resultMessage)

        }
    }
}