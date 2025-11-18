package com.example.plugd.ui.screens.plug

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.plugd.R

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