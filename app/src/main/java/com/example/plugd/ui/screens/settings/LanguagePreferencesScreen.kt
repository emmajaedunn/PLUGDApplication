package com.example.plugd.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plugd.MainActivity
import com.example.plugd.R
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.ui.utils.AppLanguageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePreferencesScreen(navController: NavController) {
    val context = LocalContext.current

    val languages = listOf(
        "English" to "en",
        "Afrikaans" to "af",
        "Xhosa" to "xh"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.language_preferences)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.language_preferences),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Telegraf,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            languages.forEach { (name, code) ->
                Button(
                    onClick = {
                        // 1️⃣ Set + persist locale (no restart)
                        AppLanguageHelper.setAppLocale(context, code)
                        // 2️⃣ Just go back – UI will recompose with new strings
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(name)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}