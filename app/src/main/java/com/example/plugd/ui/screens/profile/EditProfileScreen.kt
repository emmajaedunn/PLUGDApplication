package com.example.plugd.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.plugd.R
import com.example.plugd.viewmodels.EventViewModel
import com.example.plugd.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    eventViewModel: EventViewModel
) {
    val profileState by profileViewModel.profile.collectAsState()
    val userEvents by eventViewModel.userEvents.collectAsState()

    val scope = rememberCoroutineScope()

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var bio by remember { mutableStateOf(profileState?.bio ?: "") }
    var spotifyLink by remember { mutableStateOf(profileState?.socials?.get("spotify") ?: "") }
    var appleMusicLink by remember { mutableStateOf(profileState?.socials?.get("appleMusic") ?: "") }
    var tiktokLink by remember { mutableStateOf(profileState?.socials?.get("tiktok") ?: "") }
    var instagramLink by remember { mutableStateOf(profileState?.socials?.get("instagram") ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    // Update the local state when the profile loads from the ViewModel
    LaunchedEffect(profileState) {
        profileState?.let {
            bio = it.bio ?: ""
            spotifyLink = it.socials["spotify"] ?: ""
            appleMusicLink = it.socials["appleMusic"] ?: ""
            tiktokLink = it.socials["tiktok"] ?: ""
            instagramLink = it.socials["instagram"] ?: ""
            it.profilePictureUrl?.let { url ->
                photoUri = Uri.parse(url)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                // Save bio
                                profileViewModel.updateProfileField("bio", bio)

                                // Save socials
                                val newSocials = mapOf(
                                    "spotify" to spotifyLink,
                                    "appleMusic" to appleMusicLink,
                                    "tiktok" to tiktokLink,
                                    "instagram" to instagramLink
                                )
                                profileViewModel.updateSocials(newSocials)

                                // Save profile picture if one is selected
                                photoUri?.let { uri ->
                                    profileViewModel.uploadProfilePicture(uri)
                                }

                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Save", fontWeight = FontWeight.W500)
                    }
                }
            )
        }
    ) { innerPadding ->
        // The LazyColumn and its content remain exactly the same as before.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Image(
                    painter = if (photoUri != null) {
                        rememberAsyncImagePainter(model = photoUri)
                    } else {
                        painterResource(id = R.drawable.profile_placeholder)
                    },
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            }
            item {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Social Links", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = spotifyLink,
                        onValueChange = { spotifyLink = it },
                        label = { Text("Spotify Profile URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = appleMusicLink,
                        onValueChange = { appleMusicLink = it },
                        label = { Text("Apple Music Profile URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tiktokLink,
                        onValueChange = { tiktokLink = it },
                        label = { Text("TikTok Profile URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = instagramLink,
                        onValueChange = { instagramLink = it },
                        label = { Text("Instagram Profile URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Text("Your PLUGS", style = MaterialTheme.typography.titleMedium)
            }

            if (userEvents.isEmpty()) {
                item {
                    Text(
                        "You haven't created any plugs yet.",
                        color = Color.Gray,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                }
            } else {
                items(userEvents) { event ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(event.name, fontWeight = FontWeight.SemiBold)
                            Row {
                                // This is the corrected TextButton
                                TextButton(onClick = { navController.navigate("editEvent/${event.eventId}") }) {
                                    Text("Edit", color = MaterialTheme.colorScheme.background, fontWeight = FontWeight.W800)
                                }
                                TextButton(
                                    onClick = { eventViewModel.deleteEvent(event.eventId) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Delete", fontWeight = FontWeight.W800)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}