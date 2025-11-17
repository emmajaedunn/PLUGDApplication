package com.example.plugd.ui.screens.plug

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.AddTopBar
import com.example.plugd.viewmodels.EventViewModel
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AddPlugScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    profileViewModel: ProfileViewModel
) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid ?: ""
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var currentUserUsername by remember { mutableStateOf("") }
    var currentUserName by remember { mutableStateOf("") }
    var userLoaded by remember { mutableStateOf(false) }

    var supportDocsUrl by remember { mutableStateOf<String?>(null) }

    // Fetch username & name from Firestore (unchanged)
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener { doc ->
                    currentUserUsername = doc.getString("username") ?: ""
                    currentUserName = doc.getString("name") ?: ""
                    userLoaded = true
                }
                .addOnFailureListener {
                    currentUserUsername = ""
                    currentUserName = ""
                    userLoaded = true
                }
        }
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Location + coords
    var location by remember { mutableStateOf("") }
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }

    // Inputs
    var pluggingWhat by remember { mutableStateOf("") }
    var plugCategory by remember { mutableStateOf("") }
    var plugTitle by remember { mutableStateOf("") }
    var plugDescription by remember { mutableStateOf("") }
    var supportDocs by remember { mutableStateOf<String?>(null) }

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        scope.launch {
            try {
                val fileId = UUID.randomUUID().toString()
                val ref = storage.reference
                    .child("event_support_docs/$currentUserId/$fileId")

                // Upload file
                ref.putFile(uri).await()

                // Get public download URL
                val downloadUrl = ref.downloadUrl.await().toString()
                supportDocsUrl = downloadUrl
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Places autocomplete launcher
    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            location = place.name ?: ""
            selectedLat = place.latLng?.latitude
            selectedLng = place.latLng?.longitude
        } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR && result.data != null) {
            val status = Autocomplete.getStatusFromIntent(result.data!!)
            Log.e("AddPlugScreen", "Autocomplete error: ${status.statusMessage}")
        }
    }

    Scaffold(
        topBar = { AddTopBar(navController = navController) }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {

            PlugdAppIcon(
                modifier = Modifier
                    .size(200.dp)
                    .offset(y = (-60).dp)
            )

            Text(
                text = "Create a Plug",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-70).dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Submit an application for services, jobs, gig opportunities & collaborative opportunities.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(1.dp))

            InputField(
                label = "What are you plugging?",
                value = pluggingWhat,
                onValueChange = { pluggingWhat = it },
                placeholder = "ex. Looking for an artist for a gig on Friday."
            )

            Spacer(Modifier.height(16.dp))

            // Plug Category Dropdown
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Plug Category",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(13.dp))

                var expanded by remember { mutableStateOf(false) }
                val categories = listOf("Music", "Event", "Collab", "General", "Venue")

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = plugCategory.ifEmpty { "Select Category" },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.clickable { expanded = !expanded },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown Arrow"
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .width(200.dp)
                            .background(Color(0xFFFFF3E0)),
                        offset = DpOffset(x = 0.dp, y = 270.dp)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    plugCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            InputField(
                label = "Plug Title",
                value = plugTitle,
                onValueChange = { plugTitle = it },
                placeholder = "ex. Artist wanted"
            )

            Spacer(Modifier.height(16.dp))

            InputField(
                label = "Plug Description",
                value = plugDescription,
                onValueChange = { plugDescription = it },
                placeholder = "ex. Date: 10/07/2025 | Time: 6-8PM | Venue: Openwine"
            )

            Spacer(Modifier.height(16.dp))

            // Location with Google Places
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Location",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(13.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("ex. 31 Bree Street Cape Town") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val fields = listOf(
                                Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.LAT_LNG
                            )
                            val intent = Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.OVERLAY,
                                fields
                            ).build(context)
                            autocompleteLauncher.launch(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.btn_search),
                                contentDescription = "Search place"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Upload Support Docs
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Upload Support Docs",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(13.dp))

            FilePickerField(
                supportDocsUrl = supportDocsUrl,
                onPickClick = { filePickerLauncher.launch("*/*") }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val newEvent = EventEntity(
                        eventId = UUID.randomUUID().toString(),
                        name = plugTitle,
                        category = plugCategory,
                        description = plugDescription,
                        location = location,
                        latitude = selectedLat,
                        longitude = selectedLng,
                        date = System.currentTimeMillis(),
                        createdBy = currentUserUsername,
                        createdByName = currentUserName,
                        supportDocs = supportDocsUrl,
                        userId = currentUserId
                    )

                    scope.launch {
                        try {
                            eventViewModel.addEvent(newEvent)
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = userLoaded
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun FilePickerField(
    supportDocsUrl: String?,
    onPickClick: () -> Unit
) {
    val label = remember(supportDocsUrl) {
        if (supportDocsUrl.isNullOrBlank()) {
            "No file selected"
        } else {
            val afterSlash = supportDocsUrl.substringAfterLast("%2F")
                .substringAfterLast("/")
            val clean = afterSlash.substringBefore("?")
            try {
                java.net.URLDecoder.decode(clean, "UTF-8")
            } catch (e: Exception) {
                clean
            }
        }
    }

    OutlinedTextField(
        value = label,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPickClick() },
        trailingIcon = {
            IconButton(onClick = onPickClick) {
                Icon(
                    painter = painterResource(id = R.drawable.btn_attach),
                    contentDescription = "Pick file"
                )
            }
        }
    )
}

@Composable
fun PlugdAppIcon(modifier: Modifier = Modifier, isDarkMode: Boolean = isSystemInDarkTheme()) {
    val iconRes = if (isDarkMode) R.drawable.plugd_dark_icon else R.drawable.plugd_icon
    Image(painter = painterResource(id = iconRes), contentDescription = "PLUGD App Icon", modifier = modifier)
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String = "") {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(13.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EventItem(event: EventEntity, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = event.createdByName,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = "@${event.createdByUsername}",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFF9800)
            ),
            modifier = Modifier.clickable {
                navController.navigate("userProfile/${event.userId}")
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = event.name,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = event.description,
            style = MaterialTheme.typography.bodySmall
        )
    }
}