package com.example.plugd.ui.screens.plug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.data.localRoom.entity.EventEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlugDetailsScreen(
    navController: NavHostController,
    event: EventEntity
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title */ },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.material3.Text(
                text = "Title: ${event.name}",
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium
            )
            androidx.compose.material3.Text(
                text = "Category: ${event.category}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
            androidx.compose.material3.Text(
                text = "Description: ${event.description}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
            androidx.compose.material3.Text(
                text = "Location: ${event.location}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
            androidx.compose.material3.Text(
                text = "Created by: ${event.createdBy}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
            androidx.compose.material3.Text(
                text = "Date: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(event.date))}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )

            event.supportDocs?.let { docUri ->
                val context = LocalContext.current
                Text(
                    text = "Support document: ${docUri.substringAfterLast("/")}",
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(docUri), "*/*")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}
