package com.example.plugd.data.mappers

import com.example.plugd.model.Post
import java.text.SimpleDateFormat
import java.util.*

fun Map<String, Any>.toPost(): Post {
    val dateString = this["dateTime"] as? String
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    val date = try {
        dateString?.let { formatter.parse(it) } ?: Date()
    } catch (e: Exception) {
        Date()
    }

    return Post(
        postId = this["postId"] as? String ?: "",
        userId = this["userId"] as? String ?: "",
        communityId = this["communityId"] as? String ?: "",
        text = this["text"] as? String ?: "",
        mediaUrl = this["mediaUrl"] as? String,
        dateTime = date
    )
}