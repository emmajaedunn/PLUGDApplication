package com.example.plugd.ui.screens.nav

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerProfileTopBar(navController: NavController) {
    TopAppBar(
        title = { /* No title */ },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.btn_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Black
        )
    )
}