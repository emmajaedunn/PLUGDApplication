package com.example.plugd.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.ui.utils.PreviewNavController

@Composable
fun RoleSelectionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // --- App icon ---
        Image(
            painter = painterResource(id = R.drawable.plugd_icon),
            contentDescription = "PLUGD App Icon",
            modifier = Modifier
                .size(300.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Title ---
        Text(
            text = "What's your PLUGD role?",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = Telegraf,
                fontWeight = FontWeight.W700, // UltraBold
                fontSize = 30.sp,
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Description ---
        Text(
            text = "Select your purpose.",
            fontFamily = Telegraf,
            fontWeight = FontWeight.W400, // UltraLight
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Buttons ---
        RoleButton(text = "ARTIST") { navController.navigate(Routes.LOGIN) }
        Spacer(modifier = Modifier.height(16.dp))
        RoleButton(text = "EVENT/VENUE") { navController.navigate(Routes.LOGIN) }
        Spacer(modifier = Modifier.height(16.dp))
        RoleButton(text = "FAN") { navController.navigate(Routes.LOGIN) }
    }
}

@Composable
fun RoleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.Black, // button background
            contentColor = androidx.compose.ui.graphics.Color.White // text color
        )
    )  {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Telegraf,
                fontWeight = FontWeight.W800, // UltraBold
                fontSize = 18.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoleSelectionScreen() {
    RoleSelectionScreen(navController = PreviewNavController())
}