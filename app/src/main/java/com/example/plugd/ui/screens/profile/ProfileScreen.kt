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