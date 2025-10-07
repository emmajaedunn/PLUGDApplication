package com.example.plugd.ui.screens.nav

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsTopBar(
    navController: NavController,
    channelName: String
) {
    TopAppBar(
        title = {
            Text(
                text = channelName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.btn_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("settings_channel")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.btn_settings),
                    contentDescription = "Channel Settings",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}