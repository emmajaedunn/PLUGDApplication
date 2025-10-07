package com.example.plugd.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.plugd.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plugd.ui.screens.nav.ProfileTopBar
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    navController: NavController,
    onSettingsClick: () -> Unit = {}
) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userProfile by profileViewModel.profile.collectAsState()

   // val userProfile by profileViewModel.profile.collectAsState()
    //val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Load profile when screen opens
    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            profileViewModel.loadProfile()
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Image
                val imagePainter = painterResource(id = R.drawable.placeholder_profile) // replace with remote if available
                Image(
                    painter = imagePainter,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Welcome Back,", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(userProfile?.username ?: "Loading...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("• ${userProfile?.location ?: "Unknown"}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bio
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Bio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        userProfile?.bio ?: "No bio",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Followers (example)
            Text("Followers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_profile),
                        contentDescription = "Follower",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .padding(4.dp)
                    )
                }
                Text("+50", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Music & Socials
            Text("Music & Socials", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                SocialIcon(R.drawable.ic_spotify, "Spotify")
                SocialIcon(R.drawable.ic_applemusic, "Apple Music")
                SocialIcon(R.drawable.ic_tiktok, "TikTok")
                SocialIcon(R.drawable.ic_instagram, "Instagram")
                SocialIcon(R.drawable.ic_facebook, "Facebook")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upcoming Events
            Text("Upcoming Events", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            val events = userProfile?.events ?: emptyList()
            Row(modifier = Modifier.fillMaxWidth()) {
                events.forEach { event ->
                    EventCard(event.eventName, event.organizerId?: "Unknown", R.drawable.disco)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }

}

@Composable
fun SocialIcon(iconRes: Int, desc: String) {
    IconButton(onClick = { /* Navigate */ }) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = desc,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
fun EventCard(title: String, artist: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                Text(title, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text("by $artist", fontSize = 10.sp, color = Color.White)
            }
        }
    }
}

















/*package com.example.plugd.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plugd.ui.screens.nav.ProfileTopBar
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUser(userId)
        }
    }

    val user by viewModel.user.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = { ProfileTopBar(navController) }
    ) { padding ->
        when {
            loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            user != null -> {
                val u = user!!
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile picture + username
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(80.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", Modifier.size(50.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(u.username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(u.role ?: "User", fontSize = 14.sp, color = Color.DarkGray)
                            Text("• ${u.location ?: "Unknown"}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    // Bio
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Bio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(u.bio ?: "No bio available", fontSize = 13.sp, color = Color.DarkGray)
                        }
                    }

                    // Followers count
                    Text("Followers: ${u.followersCount ?: 0}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            else -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(error ?: "User not found")
            }
        }
    }
}

*/