package com.example.plugd.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Channel(
    val id: String = "",
    val name: String = ""
)