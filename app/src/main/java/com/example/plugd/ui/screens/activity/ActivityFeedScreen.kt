package com.example.plugd.ui.screens.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.example.plugd.ui.screens.nav.ActivityTopBar
import com.example.plugd.viewmodels.ActivityFeedViewModel
import com.example.plugd.viewmodels.ProfileViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun ActivityFeedScreen(
    navController: NavHostController,
    activityViewModel: ActivityFeedViewModel,
    profileViewModel: ProfileViewModel, // Added to handle follow actions
) {
    val activities by activityViewModel.activities.collectAsState()
    val error by activityViewModel.error.collectAsState()
    val loggedInUserProfile by profileViewModel.profile.collectAsState()

    Scaffold(
        topBar = { ActivityTopBar(navController = navController) }
    ) { innerPadding ->
        when {
            // Error State
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }

            // Empty State
            activities.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No activity yet", fontSize = 16.sp, color = Color.Gray)
                }
            }

            // Content State
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activities) { activity ->
                        val isFollowing = loggedInUserProfile?.following?.contains(activity.fromUserId) == true
                        val isOwnActivity = loggedInUserProfile?.userId == activity.fromUserId

                        ActivityItem(
                            activity = activity,
                            isFollowing = isFollowing,
                            isOwnActivity = isOwnActivity,
                            onProfileClick = { userId ->
                                navController.navigate("userProfile/$userId")
                            },
                            onFollowClick = { userId ->
                                profileViewModel.toggleFollow(userId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItem(
    activity: ActivityEntity,
    isFollowing: Boolean,
    isOwnActivity: Boolean,
    onProfileClick: (String) -> Unit,
    onFollowClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clickable { onProfileClick(activity.fromUserId) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Picture
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Message + Timestamp
        Column(modifier = Modifier.weight(1f)) {
            // --- THIS IS THE FIX ---
            // The message now uses the `fromUsername` field.
            val message = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(activity.fromUsername)
                    append(" ")
                }
                append(activity.message)
            }
            Text(text = message, fontSize = 14.sp)
            Text(
                text = DateFormat.getDateTimeInstance().format(Date(activity.timestamp)),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Follow/Following Button
        if (activity.type == "follow" && !isOwnActivity) {
            Button(
                onClick = { onFollowClick(activity.fromUserId) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFollowing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = if (isFollowing) "Following" else "Follow",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
