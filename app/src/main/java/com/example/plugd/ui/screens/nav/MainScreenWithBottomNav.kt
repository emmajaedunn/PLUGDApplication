package com.example.plugd.ui.screens.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainScreenWithBottomNav(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val items = BottomNavBar.items
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 🔎 Shared search state (so top bar search works everywhere)
    var searchQuery = ""

    Scaffold(
        topBar = {
            TopBar(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onMenuClick = { /* TODO: open filters/settings */ }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent // Transparent background
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        icon = {
                            when {
                                item.iconVector != null -> Icon(
                                    item.iconVector,
                                    contentDescription = item.label,
                                    tint = if (selected) Color.Black else Color.Gray
                                )
                                item.iconDrawable != null -> Icon(
                                    painter = painterResource(id = item.iconDrawable),
                                    contentDescription = item.label,
                                    tint = if (selected) Color.Black else Color.Gray
                                )
                            }
                        },
                        label = {
                            Text(
                                item.label,
                                color = if (selected) Color.Black else Color.Gray
                            )
                        },
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent // removes purple indicator behind icons
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}