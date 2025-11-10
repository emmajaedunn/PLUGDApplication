package com.example.plugd.ui.screens.activity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plugd.data.localRoom.entity.ActivityEntity
import java.text.DateFormat
import java.util.Date

@Composable
fun ActivityFeedItem(activity: ActivityEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(activity.message, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = DateFormat.getDateTimeInstance().format(Date(activity.timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}