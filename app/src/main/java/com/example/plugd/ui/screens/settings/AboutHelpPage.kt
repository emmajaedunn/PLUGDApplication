package com.example.plugd.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                text = "THE PLUGD ABOUT & HELP",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = Telegraf
                )
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = "About PLUGD",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "PLUGD is your one-stop platform for discovering, sharing, and connecting with the latest trends, events, and creative communities. We aim to bridge technology and creativity to help users express and discover ideas more easily.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(1.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = "Support at PLUGD",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Need help or have feedback? We're here for you!",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "📧 Email: support@plugd.app",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "🌐 Website: www.plugd.app",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "☎️ Phone: +27 (76) 151 6218",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "📱 Follow us: @plugd_app on Instagram & X",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(1.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = "App: Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}