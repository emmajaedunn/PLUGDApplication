package com.example.plugd.ui.screens.community

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.model.Channel
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.home.EventCard
import com.example.plugd.ui.screens.nav.CommunityTopBar
import com.example.plugd.ui.screens.nav.HomeTopBar
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavHostController, viewModel: ChatViewModel) {
    val allChannels by viewModel.channels.collectAsState()

    // Load channels when screen opens
    LaunchedEffect(Unit) { viewModel.loadChannels() }

    var selectedTab by remember { mutableStateOf(0) } // 0 = My Feed, 1 = My Communities

    Scaffold(
        topBar = { CommunityTopBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "THE PLUG COMMUNITY",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = Telegraf
                )
            )

            Text(
                text = "All Communities",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Horizontal list of rounded channel cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allChannels) { channel ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(
                                    "chat/${channel.id}/${Uri.encode(channel.name)}"
                                )
                            }) {
                        Card(
                            shape = CircleShape,
                            modifier = Modifier.size(80.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Show first letter as placeholder
                                Text(
                                    text = channel.name.first().toString(),
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = channel.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

         /*   Spacer(modifier = Modifier.height(16.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("My Feed", modifier = Modifier.padding(8.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("My Communities", modifier = Modifier.padding(8.dp))
                }
            }

            when (selectedTab) {
                // Implement feed or joined communities lists here
            }
        }
    }
}*/

























/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavHostController, viewModel: ChatViewModel) {

    val allChannels by viewModel.channels.collectAsState()
    // val userJoinedChannels by viewModel.userJoinedChannels.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = My Feed, 1 = My Communities

    Scaffold(
        topBar = { CommunityTopBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "THE PLUG COMMUNITY",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = Telegraf
                )
            )

            Text(
                text = "All Communities",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val allChannels by viewModel.channels.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadChannels() // <-- loads channels from Firestore
            }

            LazyColumn(modifier = Modifier.height(150.dp)) {
                items(allChannels) { channel ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("chat/${channel.id}/${channel.name}") }
                            .padding(8.dp)
                    ) {
                        Text(text = channel.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("My Feed", modifier = Modifier.padding(8.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("My Communities", modifier = Modifier.padding(8.dp))
                }
            }

            when (selectedTab) {
                // 0 -> MyFeedList(viewModel = viewModel, navController = navController)
                // 1 -> MyCommunitiesList(userJoinedChannels, navController)
            }
        }
    }

    /*@Composable
    fun MyFeedList(viewModel: ChatViewModel, navController: NavHostController) {
        val feed by viewModel.userFeed.collectAsState()
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(feed) { message ->
                Text(
                    text = "${message.senderName}: ${message.content}",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }*/

    @Composable
    fun MyCommunitiesList(channels: List<Channel>, navController: NavHostController) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(channels) { channel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("chat/${channel.id}/${channel.name}") }
                        .padding(8.dp)
                ) {
                    Text(text = channel.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}*/















    /*

        Text(
            text = "ALL CHANNELS",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.height(150.dp)) {
            items(allChannels) { channel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("chat/${channel.id}/${channel.name}") }
                        .padding(8.dp)
                ) {
                    Text(text = channel.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("My Feed", modifier = Modifier.padding(8.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("My Communities", modifier = Modifier.padding(8.dp))
            }
        }

        when (selectedTab) {
           // 0 -> MyFeedList(viewModel = viewModel, navController = navController)
           // 1 -> MyCommunitiesList(userJoinedChannels, navController)
        }
    }
}

/*@Composable
fun MyFeedList(viewModel: ChatViewModel, navController: NavHostController) {
    val feed by viewModel.userFeed.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
        items(feed) { message ->
            Text(
                text = "${message.senderName}: ${message.content}",
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}*/

@Composable
fun MyCommunitiesList(channels: List<Channel>, navController: NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(channels) { channel ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("chat/${channel.id}/${channel.name}") }
                    .padding(8.dp)
            ) {
                Text(text = channel.name, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}*/