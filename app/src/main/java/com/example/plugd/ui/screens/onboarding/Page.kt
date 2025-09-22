package com.example.plugd.ui.screens.onboarding

import androidx.annotation.DrawableRes
import com.example.plugd.R

data class Page(
    val title: String,
    val description: String,
    @DrawableRes val image: Int,
)

val pages = listOf(
    Page(
        "DISCOVER NEW PLUGS",
        "Explore upcoming and recommended artists, events, and opportunities nearby.",
        R.drawable.onboarding_discover
    ),
    Page(
        "PLUG FRIENDS INTO YOUR WORLD",
        "Share your events or creative work with your friends and followers, connect instantly.",
        R.drawable.onboarding_friends
    ),
    Page(
        "STAY PLUGGED IN THE CURRENT",
        "Get real-time updates, notifications, and recommendations from events and artists you love.",
        R.drawable.onboarding_current
    ),
    Page(
        "PLUG IN TO THE COMMUNITY",
        "Join a network of artists, fans, and event organisers. Find your vibrant community and find your purpose.",
        R.drawable.onboarding_community
    ),
    Page(
        "READY TO PLUG IN?",
        "Jump straight into the PLUGD app and start connecting!",
        R.drawable.onboarding_ready
    )
)