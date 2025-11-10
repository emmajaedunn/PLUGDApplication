/*package com.example.plugd.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.theme.Telegraf

@Composable
fun RoleSelectionScreen(
    navController: NavHostController,
    onRoleSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.plugd_icon),
            contentDescription = "PLUGD App Icon",
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "What's your PLUGD role?",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.W700
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select your purpose.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.W400
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        RoleButton("ARTIST") { onRoleSelected("Artist") }
        RoleButton("EVENT/VENUE") { onRoleSelected("EventVenue") }
        RoleButton("FAN") { onRoleSelected("Fan") }
    }
}

@Composable
fun RoleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.Black,
            contentColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium.copy(fontFamily = Telegraf))
    }
}*/