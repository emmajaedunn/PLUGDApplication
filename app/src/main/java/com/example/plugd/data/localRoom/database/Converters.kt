package com.example.plugd.data.localRoom.database

import androidx.room.TypeConverter
import com.example.plugd.model.SpotifyPlaylistEmbedded
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // Spotify Playlist Converter
    @TypeConverter
    fun fromPlaylists(list: List<SpotifyPlaylistEmbedded>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    // Convert JSON string to list of SpotifyPlaylistEmbedded objects
    @TypeConverter
    fun toPlaylists(json: String?): List<SpotifyPlaylistEmbedded> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<SpotifyPlaylistEmbedded>>() {}.type
        return gson.fromJson(json, type)
    }

    // Map Converter
    @TypeConverter
    fun fromString(value: String?): Map<String, String>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    // Convert map to JSON string
    @TypeConverter
    fun fromMap(map: Map<String, String>?): String? {
        return gson.toJson(map)
    }
}