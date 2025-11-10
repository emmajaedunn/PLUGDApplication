/*package com.example.plugd.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plugd.R
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.model.Event
import com.example.plugd.model.UserProfile
import com.example.plugd.ui.screens.nav.ViewerProfileTopBar
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    targetUserId: String,
    profileViewModel: ProfileViewModel,
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    val userProfile by profileViewModel.profile.collectAsState()
    val isFollowing by profileViewModel.isFollowing.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val scope = rememberCoroutineScope()

    // Load profile & events
    LaunchedEffect(targetUserId) {
        profileViewModel.loadProfile(targetUserId)
        profileViewModel.loadUserEvents(targetUserId)
    }

    Scaffold(
        topBar = { ViewerProfileTopBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                userProfile?.let { user ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- Profile Header ---
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile_placeholder),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(user.username ?: "Unknown", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("• ${user.name ?: "No name"}", fontSize = 12.sp, color = Color.Gray)
                                Text("• ${user.location ?: "Unknown"}", fontSize = 12.sp, color = Color.Gray)
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            if (currentUserId != targetUserId) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            profileViewModel.toggleFollow(targetUserId)
                                            profileViewModel.updateFollowersLocally(!isFollowing, currentUserId!!)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFollowing) Color.Gray else MaterialTheme.colors.onPrimary
                                    )
                                ) {
                                    Text(if (isFollowing) "Following" else "Follow")
                                }
                            }
                        }

                        // --- Bio ---
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color.DarkGray.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Bio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(user.bio ?: "No bio", fontSize = 13.sp, color = if (isDarkMode) Color.White else Color.DarkGray)
                            }
                        }

                        // --- Followers ---
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color.DarkGray.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Followers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                val followers = user.followers ?: emptyList()
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    followers.forEach { _ ->
                                        Image(
                                            painter = painterResource(id = R.drawable.profile_placeholder),
                                            contentDescription = "Follower",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .padding(end = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // --- Music & Socials ---
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color.DarkGray.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Music & Socials", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    SocialIcon(R.drawable.ic_spotify, "Spotify")
                                    SocialIcon(R.drawable.ic_applemusic, "Apple Music")
                                    SocialIcon(R.drawable.ic_tiktok, "TikTok")
                                    SocialIcon(R.drawable.ic_instagram, "Instagram")
                                }
                            }
                        }

                        // --- Events ---
                        UserEventsSection(userEvents = user.events, navController = navController)
                    }
                } ?: Text("User not found")
            }
        }
    }
}


@Composable
fun UserEventsSection(
    userEvents: List<com.example.plugd.model.Event>?, // your Event model
    navController: NavController
) {
    val context = LocalContext.current

    Column {
        Text("Upcoming Events", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (userEvents.isNullOrEmpty()) {
            Text("No upcoming events", color = Color.Gray, fontSize = 12.sp)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                userEvents.forEach { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("eventDetails/${event.eventId}") },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)) // light orange
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(event.eventName, style = MaterialTheme.typography.h3, fontWeight = FontWeight.Bold)
                            Text(event.description, style = MaterialTheme.typography.h3)
                            Text("📍 ${event.location}", style = MaterialTheme.typography.h3)
                            Text("Organizer: ${event.organizerId}", style = MaterialTheme.typography.body1)

                            // If you want to show support docs later, add a nullable field to Event model
                            // and include similar clickable logic
                        }
                    }
                }
            }
        }
    }
}*/








/*package com.example.plugd.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plugd.R
import com.example.plugd.ui.screens.nav.ViewerProfileTopBar
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    targetUserId: String,
    profileViewModel: ProfileViewModel,
    isDarkMode: Boolean = false
) {
    val userProfile by profileViewModel.profile.collectAsState()
    val isFollowing by profileViewModel.isFollowing.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    val scope = rememberCoroutineScope()

    // Load target user profile and follow state
    LaunchedEffect(targetUserId) {
        profileViewModel.loadProfile(targetUserId)
        profileViewModel.loadUserEvents(targetUserId)
    }

    Scaffold(
        topBar = { ViewerProfileTopBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                userProfile?.let { user ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- Profile Header ---
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile_placeholder),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    user.username ?: "Unknown",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "• ${user.name ?: "No name"}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    "• ${user.location ?: "Unknown"}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // --- Follow Button ---
                            if (currentUserId != targetUserId) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            profileViewModel.toggleFollow(targetUserId)
                                            profileViewModel.updateFollowersLocally(!isFollowing, currentUserId!!)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFollowing) Color.Gray else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(if (isFollowing) "Following" else "Follow")
                                }
                            }
                        }

                        // --- Bio ---
                        BioCard(userProfile = userProfile, isDarkMode = isDarkMode)

                        // --- Followers Section ---
                        Text("Followers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        val followers = user.followers ?: emptyList()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            followers.forEach { _ ->
                                Image(
                                    painter = painterResource(id = R.drawable.profile_placeholder),
                                    contentDescription = "Follower",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .padding(4.dp)
                                )
                            }
                        }

                        // --- Music Links ---
                        Text("Music & Socials", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SocialIcon(R.drawable.ic_spotify, "Spotify")
                            SocialIcon(R.drawable.ic_applemusic, "Apple Music")
                            SocialIcon(R.drawable.ic_tiktok, "TikTok")
                            SocialIcon(R.drawable.ic_instagram, "Instagram")
                        }

                        // --- Events ---
                        Text("Upcoming Events", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            user.events?.forEach { event ->
                                @Composable
                                fun EventCard(
                                    event: Event,
                                    navController: NavController,
                                    currentUserId: String
                                ) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                } ?: Text("User not found")
            }
        }
    }
}
    }*/