package com.example.plugd.ui.screens.profile

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.plugd.R
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.model.SpotifyPlaylistEmbedded
import com.example.plugd.model.UserProfile
import com.example.plugd.remote.api.spotify.SpotifyPlaylist
import com.example.plugd.remote.api.spotify.startSpotifyAuth
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.ProfileTopBar
import com.example.plugd.viewmodels.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    navController: NavController,
    isDarkMode: Boolean,
    userId: String?
) {
    val userProfile by profileViewModel.profile.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val isOwnProfile by profileViewModel.isOwnProfile.collectAsState()
    val isFollowing by profileViewModel.isFollowing.collectAsState()
    val userEvents by profileViewModel.userEvents.collectAsState()

    /* Correct
    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }*/

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)

        // after loadProfile, check again if this is own profile
        if (profileViewModel.isOwnProfile.value) {
            profileViewModel.loadSpotifyPlaylists()
        }
    }

    Scaffold(
        topBar = {
            if (isOwnProfile) {
                ProfileTopBar(navController)
            } else {
                OtherUserProfileTopBar(navController)
            }
        },
        bottomBar = {
            // Bottom nav specific for this screen
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
            }
        },
    ) { innerPadding ->
        if (loading && userProfile == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (userProfile != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(0.dp))
                ProfileHeader(
                    userProfile = userProfile!!,
                    isOwnProfile = isOwnProfile,
                    isFollowing = isFollowing,
                    onFollowClick = {
                        userProfile?.userId?.let { targetId ->
                            profileViewModel.toggleFollow(targetId)
                        }
                    },
                    // --- FIX #1: The "Edit Profile" button now navigates to the edit screen ---
                    onEditProfileClick = { navController.navigate(Routes.EDIT_PROFILE) }
                )

                BioCard(userProfile = userProfile)

                FollowersSection(
                    followers = userProfile?.followers
                ) { followerId ->
                    navController.navigate("userProfile/$followerId")
                }

                FollowingSection(
                    following = userProfile?.following
                ) { followingId ->
                    navController.navigate("userProfile/$followingId")
                }

                SocialsSection(userProfile)

                SpotifyPlaylistsSection(
                    playlists = userProfile?.spotifyPlaylists.orEmpty(),
                    showSyncButton = isOwnProfile && !userProfile?.spotifyPlaylists.isNullOrEmpty(),
                    onSyncClick = { profileViewModel.loadSpotifyPlaylists() }
                )

                if (isOwnProfile && userProfile?.spotifyPlaylists.isNullOrEmpty()) {
                    ConnectSpotifyCard()
                }

                UserEventsSection(
                    userEvents = userEvents,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("User not found.")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Profile") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}


@Composable
fun ProfileHeader(
    userProfile: UserProfile,
    isOwnProfile: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val photoUrl = userProfile.profilePictureUrl ?: userProfile.profileImageUrl

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = photoUrl,
                placeholder = painterResource(id = R.drawable.profile_placeholder),
                error = painterResource(id = R.drawable.profile_placeholder)
            ),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            val username = userProfile.username ?: "Loading..."
            Text(
                text = username,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            userProfile.location?.let {
                Text(
                    "• $it",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        if (isOwnProfile) {
            OutlinedButton(onClick = onEditProfileClick) {
                Text("Edit Profile", fontWeight = FontWeight.W500)
            }
        } else {
            Button(
                onClick = onFollowClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFollowing) Color.Gray else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isFollowing) "Following" else "Follow")
            }
        }
    }
}

@Composable
fun BioCard(userProfile: UserProfile?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Bio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = userProfile?.bio ?: "No bio yet.",
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun FollowersSection(
    followers: List<String>?,
    onUserClick: (String) -> Unit
) {
    val followersCount = followers?.size ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Followers ($followersCount)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            if (!followers.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    followers.take(10).forEach { followerId ->
                        Image(
                            painter = painterResource(id = R.drawable.profile_placeholder),
                            contentDescription = "Follower",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onUserClick(followerId) }
                                .padding(end = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FollowingSection(
    following: List<String>?,
    onUserClick: (String) -> Unit
) {
    val followingCount = following?.size ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Following ($followingCount)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            if (!following.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    following.take(10).forEach { userId ->
                        Image(
                            painter = painterResource(id = R.drawable.profile_placeholder),
                            contentDescription = "Following user",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onUserClick(userId) }
                                .padding(end = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SocialsSection(userProfile: UserProfile?) {
    // The modern and recommended way to open a URL in Compose
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Music & Socials", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Get the social links from the map
                val socials = userProfile?.socials ?: emptyMap()
                val spotifyUrl = socials["spotify"]
                val appleMusicUrl = socials["apple_music"]
                val tiktokUrl = socials["tiktok"]
                val instagramUrl = socials["instagram"]

                SocialIcon(
                    iconRes = R.drawable.ic_spotify,
                    desc = "Spotify",
                    enabled = !spotifyUrl.isNullOrBlank()
                ) {
                    spotifyUrl?.let { uriHandler.openUri(it) }
                }

                SocialIcon(
                    iconRes = R.drawable.ic_applemusic,
                    desc = "Apple Music",
                    enabled = !appleMusicUrl.isNullOrBlank()
                ) {
                    appleMusicUrl?.let { uriHandler.openUri(it) }
                }

                SocialIcon(
                    iconRes = R.drawable.ic_tiktok,
                    desc = "TikTok",
                    enabled = !tiktokUrl.isNullOrBlank()
                ) {
                    tiktokUrl?.let { uriHandler.openUri(it) }
                }

                SocialIcon(
                    iconRes = R.drawable.ic_instagram,
                    desc = "Instagram",
                    enabled = !instagramUrl.isNullOrBlank()
                ) {
                    instagramUrl?.let { uriHandler.openUri(it) }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConnectSpotifyCard() {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1DB954)) // Spotify green vibe
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Connect Spotify",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                "Link your Spotify account to show your playlists on your PLUGD profile.",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { startSpotifyAuth(context) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Connect", color = Color.White)
            }
        }
    }
}

@Composable
fun SpotifyPlaylistsSection(
    playlists: List<SpotifyPlaylistEmbedded>,
    showSyncButton: Boolean,
    onSyncClick: () -> Unit
) {
    if (playlists.isEmpty() && !showSyncButton) return

    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Spotify Playlists", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            if (showSyncButton) {
                IconButton(onClick = onSyncClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        tint = Color.Black,
                        modifier = Modifier.size(19.dp),
                        contentDescription = "Sync Playlists"
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        playlists.forEach { pl ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pl.imageUrl?.let { img ->
                        Image(
                            painter = coil.compose.rememberAsyncImagePainter(img),
                            contentDescription = pl.name,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(pl.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(
                            pl.ownerName.orEmpty(),
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }

                    pl.externalUrl?.let { url ->
                        Text(
                            "Open",
                            fontSize = 12.sp,
                            color = Color(0xFF1DB954),
                            modifier = Modifier
                                .clickable { uriHandler.openUri(url) }
                                .padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SocialIcon(
    iconRes: Int,
    desc: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, enabled = enabled) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = desc,
            modifier = Modifier.size(90.dp),

        )
    }
}

@Composable
fun UserEventsSection(
    userEvents: List<EventEntity>?,
    navController: NavController
) {
    Column {
        Text("PLUGS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))

        if (userEvents.isNullOrEmpty()) {
            Text("No plugs yet", color = Color.Gray, fontSize = 12.sp)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                userEvents.forEach { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("${Routes.PLUG_DETAILS}/${event.eventId}") },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(event.name, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(event.description, maxLines = 2, fontSize = 14.sp)
                            Text("Location: ${event.location}", fontSize = 14.sp)
                            Text("Organizer: ${event.createdByName}", fontSize = 14.sp)

                            event.supportDocs?.let { docUri ->
                                Text(
                                    text = "📄 View Document",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(docUri))
                                        navController.context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}























/*package com.example.plugd.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.model.UserProfile
import com.example.plugd.ui.screens.home.EventCard
import com.example.plugd.ui.screens.nav.ProfileTopBar
import com.example.plugd.viewmodels.EventViewModel
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    navController: NavController,
    eventViewModel: EventViewModel,
    isDarkMode: Boolean
) {
    val userProfile by profileViewModel.profile.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val userEvents by profileViewModel.userEvents.collectAsState()

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        profileViewModel.loadProfile(uid)
    }

    Scaffold(
        topBar = { ProfileTopBar(navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    Text("Welcome back,", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        userProfile?.username ?: "Loading...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "• ${userProfile?.location ?: "Unknown"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // --- Bio ---
            BioCard(userProfile = userProfile, isDarkMode = isDarkMode)

            // --- Followers ---
            Text("Followers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            val followers = profileViewModel.profile.value?.followers ?: emptyList()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                followers.forEach { followerId ->
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
            UserEventsSection(
                userEvents = userProfile?.events as List<EventEntity>?,
                navController = navController,
                profileViewModel = profileViewModel
            )

            /* --- Events ---
            Text("Upcoming Events", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth()) {
                userProfile?.events?.forEach { event ->
                    EventCard(event.eventName, event.organizerId ?: "Unknown", R.drawable.disco)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }*/
            }
        }
    }


@Composable
fun BioCard(userProfile: UserProfile?, isDarkMode: Boolean) {
    val backgroundColor = if (isDarkMode) Color.DarkGray.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f)
    val textColor = if (isDarkMode) Color.White else Color.DarkGray
    val titleColor = if (isDarkMode) Color.White else Color.Black

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, if (isDarkMode) Color.Gray else Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Bio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = titleColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userProfile?.bio ?: "No bio",
                fontSize = 13.sp,
                color = textColor
            )
        }
    }
}


@Composable
fun SocialIcon(iconRes: Int, desc: String) {
    IconButton(onClick = { /* Navigate */ }) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = desc,
            modifier = Modifier.size(170.dp)
        )
    }
}

@Composable
fun UserEventsSection(
    userEvents: List<EventEntity>?,
    navController: NavController,
    profileViewModel: ProfileViewModel
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
                            Text(
                                text = event.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "📍 ${event.location}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Created by: ${event.createdByName}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            event.supportDocs?.let { docUri ->
                                Text(
                                    text = "📄 ${docUri.substringAfterLast("/")}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1565C0)),
                                    modifier = Modifier.clickable {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(Uri.parse(docUri),
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}*/















/*
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
}*/
























/*package com.example.plugd.ui.screens.profile

import androidx.compose.foundation.BorderStroke
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
import com.example.plugd.model.UserProfile
import com.example.plugd.ui.screens.nav.ProfileTopBar
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    navController: NavController,
    viewedUserId: String? = null, // 👈 add this
    onSettingsClick: () -> Unit = {},
    isDarkMode: Boolean
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
                val imagePainter = painterResource(id = R.drawable.profile_placeholder) // Default placeholder for users
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

            // Instead of the old Card block
            BioCard(userProfile = userProfile, isDarkMode = isDarkMode)

            /* working Bio
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
            }*/

            Spacer(modifier = Modifier.height(16.dp))

            // Followers (default profile picture)
            Text("Followers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) {
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

            Spacer(modifier = Modifier.height(2.dp))

            // Music & Social Platforms
            Text("Music & Socials", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                SocialIcon(R.drawable.ic_spotify, "Spotify")
                SocialIcon(R.drawable.ic_applemusic, "Apple Music")
                SocialIcon(R.drawable.ic_tiktok, "TikTok")
                SocialIcon(R.drawable.ic_instagram, "Instagram")
                SocialIcon(R.drawable.ic_facebook, "Facebook")
            }

            Spacer(modifier = Modifier.height(2.dp))

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
fun BioCard(userProfile: UserProfile?, isDarkMode: Boolean) {
    val backgroundColor = if (isDarkMode) Color.DarkGray.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f)
    val textColor = if (isDarkMode) Color.White else Color.DarkGray
    val titleColor = if (isDarkMode) Color.White else Color.Black

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, if (isDarkMode) Color.Gray else Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Bio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = titleColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userProfile?.bio ?: "No bio",
                fontSize = 13.sp,
                color = textColor
            )
        }
    }
}


@Composable
fun SocialIcon(iconRes: Int, desc: String) {
    IconButton(onClick = { /* Navigate */ }) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = desc,
            modifier = Modifier.size(170.dp)
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
}*/