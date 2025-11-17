package com.example.plugd.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.plugd.data.localRoom.entity.EventEntity

@Composable
fun EventCard(
    event: EventEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Category tag
            Box(
                modifier = Modifier
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

            Spacer(modifier = Modifier.height(8.dp))

            // Plug name
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Plug description
            if (event.description.isNotEmpty()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Plug Location
            Text(
                text = "Location: ${event.location}",
                style = MaterialTheme.typography.bodySmall
            )

            // Plug Creator
            Text(
                text = "Created by: ${event.createdByName}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}