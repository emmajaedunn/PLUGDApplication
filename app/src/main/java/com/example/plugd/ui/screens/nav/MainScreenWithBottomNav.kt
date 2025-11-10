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
    topBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    loggedInUserId: String // Pass the UID
) {
    val items = BottomNavBar.items
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = { topBar() },
        bottomBar = {
            NavigationBar(containerColor = Color.Transparent) {
                items.forEach { item ->
                    // Corrected 'selected' logic to do a simple string comparison
                    val selected = currentRoute == item.route

                    /*val selected = when (item) {
                        is BottomNavBar.Profile -> currentRoute?.startsWith("profile/") == true
                        else -> currentRoute == item.route
                    }*/

                    // ADDED
                    NavigationBarItem(
                        icon = {
                            if (item.iconVector != null) {
                                Icon(item.iconVector, contentDescription = item.label)
                            } else if (item.iconDrawable != null) {
                                Icon(painter = painterResource(id = item.iconDrawable), contentDescription = item.label)
                            }
                        },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            if (!selected) {

                                /*val routeToNavigate = when (item) {
                                    is BottomNavBar.Profile -> "profile/$loggedInUserId"
                                    else -> item.route
                                }*/

                                //old - navController.navigate(routeToNavigate) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        )
                    )

                    /* working NavigationBarItem(
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
                        label = { Text(item.label, color = if (selected) Color.Black else Color.Gray) },
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                val routeToNavigate = when (item) {
                                    is BottomNavBar.Profile -> "profile/$loggedInUserId"
                                    else -> item.route
                                }

                                navController.navigate(routeToNavigate) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )*/
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}