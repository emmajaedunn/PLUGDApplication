package com.example.plugd.ui.screens.onboarding.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.text.font.FontWeight
import com.example.plugd.ui.theme.Telegraf

@Composable
fun PLUGDButton(
    text: String = "",
    onClick: () -> Unit,
    backgroundColor: Color = Color.Black,
    contentColor: Color = Color.White,
    size: Dp = 20.dp, // default size for circular button
    isCircular: Boolean = false
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = if (isCircular) CircleShape else androidx.compose.foundation.shape.RoundedCornerShape(3.dp),
        modifier = if (isCircular) Modifier.size(size) else Modifier
    ) {
        if (text.isEmpty()) {
            // Show default arrow if text is empty
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                tint = contentColor
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = contentColor,
                fontFamily = Telegraf
            )
        }
    }
}

@Composable
fun PLUGDTextButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}