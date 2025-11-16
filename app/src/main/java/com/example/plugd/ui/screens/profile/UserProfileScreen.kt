/*package com.example.plugd.ui.screens.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.plugd.R
import com.example.plugd.model.UserProfile
import com.example.plugd.viewmodels.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    userId: String
) {
    val profileState by profileViewModel.profile.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val isOwnProfile by profileViewModel.isOwnProfile.collectAsState()

    // Load the requested user's profile when we arrive here
    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        when {
            loading && profileState == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            profileState != null -> {
                val user = profileState!!
                UserProfileContent(
                    navController = navController,
                    userProfile = user,
                    isOwnProfile = isOwnProfile,
                    onFollowClick = {
                        user.userId.takeIf { it.isNotBlank() }?.let { targetId ->
                            profileViewModel.toggleFollow(targetId)
                        }
                    }
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("User not found.")
                }
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    navController: NavHostController,
    userProfile: UserProfile,
    isOwnProfile: Boolean,
    onFollowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // ---------- HEADER (Avatar + name + username) ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = userProfile.profilePictureUrl ?: "",
                    placeholder = painterResource(id = R.drawable.profile_placeholder),
                    error = painterResource(id = R.drawable.profile_placeholder)
                ),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userProfile.name?.takeIf { it.isNotBlank() }
                        ?: userProfile.username?.takeIf { it.isNotBlank() }
                        ?: "User",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (!userProfile.username.isNullOrBlank()) {
                    Text(
                        text = "@${userProfile.username}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary // your orange
                    )
                }
                if (!userProfile.location.isNullOrBlank()) {
                    Text(
                        text = userProfile.location!!,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // ---------- ACTION BUTTONS ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isOwnProfile) {
                // For your own profile – Edit + My Plugs
                OutlinedButton(
                    onClick = { /* navController.navigate(Routes.EDIT_PROFILE) */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit Profile", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = { /* navController.navigate("user_plugs/${userProfile.userId}") */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("My Plugs", fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = onFollowClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Follow", fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { /* TODO: open DM / chat with this user */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Message", fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { /* navController.navigate("user_plugs/${userProfile.userId}") */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Plugs", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // ---------- BIO ----------
        if (!userProfile.bio.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.04f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Bio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = userProfile.bio!!,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // ---------- SOCIALS ----------
        if (userProfile.socials.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.04f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Socials",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    userProfile.socials.forEach { (platform, url) ->
                        Text(
                            text = "$platform: $url",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // You can add a "User’s plugs" preview list here if you want later.
    }
}*/