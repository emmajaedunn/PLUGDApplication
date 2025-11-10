package com.example.plugd.ui.screens.plug

import android.net.Uri
import android.util.Log
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
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.AddTopBar
import com.example.plugd.viewmodels.EventViewModel
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.android.libraries.places.api.Places
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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Place this near your other state variables in AddPlugScreen
    var location by remember { mutableStateOf("") }
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }

    // State for Firestore user data
    var currentUserUsername by remember { mutableStateOf("") }
    var currentUserName by remember { mutableStateOf("") }
    var userLoaded by remember { mutableStateOf(false) }

    // Fetch username and full name from Firestore
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

    // Input state
    var pluggingWhat by remember { mutableStateOf("") }
    var plugCategory by remember { mutableStateOf("") }
    var plugTitle by remember { mutableStateOf("") }
    var plugDescription by remember { mutableStateOf("") }
    var placeLocation by remember { mutableStateOf("") }
    var supportDocs by remember { mutableStateOf<String?>(null) }

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { supportDocs = it.toString() }
    }

    // Create a launcher for Autocomplete
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

                Box(modifier = Modifier.width(1000.dp)) {
                    OutlinedTextField(
                        value = plugCategory.ifEmpty { "Select Category" },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .clickable { expanded = !expanded },
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

            // Replace the previous Location InputField block with this:
            Spacer(Modifier.height(16.dp))

            // Google Places Autocomplete for Location
            val context = LocalContext.current
            val AUTOCOMPLETE_REQUEST_CODE = 1

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Location",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(13.dp))

                // Location input field
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it }, // allows typing manually
                    placeholder = { Text("ex. 31 Bree Street Cape Town") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val fields =
                                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
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




            /*Spacer(Modifier.height(16.dp))

          InputField(
                label = "Location",
                value = location,
                onValueChange = { location = it },
                placeholder = "ex. 31 Bree Street Cape Town"
            )*/

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

            FilePickerField(supportDocs = supportDocs) { uri ->
                supportDocs = uri.toString()
            }

            Spacer(Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    val newEvent = EventEntity(
                        eventId = UUID.randomUUID().toString(),
                        name = plugTitle,
                        category = plugCategory,
                        description = plugDescription,
                        location = location,
                        latitude = selectedLat,           // from GPS or Places API
                        longitude = selectedLng,
                        date = System.currentTimeMillis(),
                        createdBy = currentUserUsername,
                        createdByName = currentUserName,
                        supportDocs = supportDocs?.takeIf { it.isNotEmpty() },
                        userId = currentUserId ?: ""
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
                enabled = userLoaded // prevent submission before user data is fetched
            ) {
                Text("Submit")
            }
        }
    }
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
        OutlinedTextField(value = value, onValueChange = onValueChange, placeholder = { Text(placeholder) }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun EventItem(event: EventEntity, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Clickable username
        Text(
            text = event.createdBy, // username
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            ),
            modifier = Modifier.clickable {
                // Navigate to user profile screen
                navController.navigate("userProfile/${event.createdBy}")
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


























/*package com.example.plugd.ui.screens.plug

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.AddTopBar
import com.example.plugd.viewmodels.EventViewModel
import kotlinx.coroutines.launch
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import com.example.plugd.R
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@Composable
fun AddPlugScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
) {


    // WORKS IN CHAT SCREEN JUST ADDED
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid


    // val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown"

    //val currentUser = FirebaseAuth.getInstance().currentUser
    // JUST ADDED
    // val currentUserId = currentUser?.uid


    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // WB val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown"
    // WORKED BEFORE val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Remember input state
    var pluggingWhat by remember { mutableStateOf("") }
    var plugCategory by remember { mutableStateOf("") }
    var plugTitle by remember { mutableStateOf("") }
    var plugDescription by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var supportDocs by remember { mutableStateOf<String?>(null) }


    // Launcher to pick a file
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->  // Explicitly specify the type
        uri?.let {
            supportDocs = it.toString()
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

            /* working Image(
                painter = painterResource(id = R.drawable.plugd_icon),
                contentDescription = "PLUGD App Icon",
                modifier = Modifier
                    .size(200.dp)
                    .offset(y = (-60).dp)
            )
             */
            // Title
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

            // Description
            Text(
                text = "Submit an application for services, jobs, gig opportunities & collaborative opportunities.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(1.dp))

            // Input Fields
            InputField(
                label = "What are you plugging?",
                value = pluggingWhat,
                onValueChange = { pluggingWhat = it },
                placeholder = "ex. Looking for an artist for a gig on Friday."
            )

            Spacer(Modifier.height(16.dp))

            // Plug Category Dropdown
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
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
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
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
                            .background(CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)).containerColor),
                        offset = DpOffset(x = 0.dp, y = 270.dp)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                },
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

            InputField(
                label = "Location",
                value = location,
                onValueChange = { location = it },
                placeholder = "ex. 31 Bree Street Cape Town"
            )

            Spacer(Modifier.height(16.dp))

            // Upload Support Docs
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Upload Support Docs",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(13.dp))

            FilePickerField(
                supportDocs = supportDocs
            ) { uri ->
                supportDocs = uri.toString()
            }

            Spacer(Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    val newEvent = EventEntity(
                        eventId = UUID.randomUUID().toString(),
                        name = plugTitle,
                        category = plugCategory,
                        description = plugDescription,
                        location = location,
                        date = System.currentTimeMillis(),
                        createdBy = currentUserId,        // UID
                        createdByName = currentUserName,
                        supportDocs = supportDocs?.takeIf { it.isNotEmpty() }
                    )

                    scope.launch {
                        try {
                            eventViewModel.addEvent(newEvent)
                            // Refresh events after adding

                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}

// Dark mode
@Composable
fun PlugdAppIcon(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme()
) {
    val iconRes = if (isDarkMode) R.drawable.plugd_dark_icon else R.drawable.plugd_icon

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = "PLUGD App Icon",
        modifier = modifier
    )
}

// Reusable Input Field
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String = "") {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(13.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}*/