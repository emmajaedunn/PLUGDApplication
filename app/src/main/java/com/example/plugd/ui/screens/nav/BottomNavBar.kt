package com.example.plugd.ui.screens.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.plugd.R

sealed class BottomNavBar(
    val route: String,
    val label: String,
    val iconVector: ImageVector? = null,
    val iconDrawable: Int? = null
) {
    object Home : BottomNavBar("home_screen", "Home", iconVector = Icons.Default.Home)
    object Community : BottomNavBar("community_screen", "Community", iconDrawable = R.drawable.ic_channel)
    object Add : BottomNavBar("add_screen", "Add", iconVector = Icons.Default.Add)
    object Activity : BottomNavBar("activity_screen", "Activity", iconDrawable = R.drawable.ic_activity)
    object Profile : BottomNavBar("profile_screen", "Profile", iconVector = Icons.Default.Person)

    companion object {
        val items = listOf(Home, Community, Add, Activity, Profile)
    }
}