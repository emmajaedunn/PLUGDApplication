package com.example.plugd.ui.screens.plug

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.ui.navigation.Routes
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlugDetailsScreen(
    navController: NavHostController,
    event: EventEntity
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val eventDate = dateFormat.format(Date(event.date))

    val supportLabel = remember(event.supportDocs) {
        event.supportDocs?.let { url ->
            val afterSlash = url.substringAfterLast("%2F").substringAfterLast("/")
            val clean = afterSlash.substringBefore("?")
            try {
                URLDecoder.decode(clean, "UTF-8")
            } catch (e: Exception) {
                clean
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PLUG Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_back),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category pill
                    if (event.category.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = event.category,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    // Title
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    // Description
                    if (event.description.isNotEmpty()) {
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Location + Date
                    Text(
                        text = "📍 ${event.location}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Text(
                        text = "🗓 $eventDate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    // Support Docs
                    if (!event.supportDocs.isNullOrBlank() && supportLabel != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Supporting Docs:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = "📄 $supportLabel",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(Uri.parse(event.supportDocs), "*/*")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Created by
                    Text(
                        text = "Created by:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    // Full name
                    if (event.createdByName.isNotBlank()) {
                        Text(
                            text = event.createdByName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    // Username
                    if (event.createdBy.isNotBlank()) {
                        Text(
                            text = "@${event.createdBy}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    Routes.USER_PROFILE.replace("{userId}", event.userId)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
