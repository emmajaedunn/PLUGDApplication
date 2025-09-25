package com.example.plugd.repository

import com.example.plugd.model.Channel
import com.example.plugd.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getChannels(): Flow<List<Channel>> = callbackFlow {
        val listener = db.collection("channels")
            .addSnapshotListener { snapshot, _ ->
                val channels = snapshot?.documents?.map {
                    it.toObject(Channel::class.java)!!.copy(id = it.id)
                } ?: emptyList()
                trySend(channels)
            }
        awaitClose { listener.remove() }
    }

    fun getMessages(channelId: String): Flow<List<Message>> = callbackFlow {
        val listener = db.collection("channels")
            .document(channelId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.map {
                    it.toObject(Message::class.java)!!.copy(id = it.id)
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    fun sendMessage(channelId: String, message: Message) {
        db.collection("channels")
            .document(channelId)
            .collection("messages")
            .add(message)
    }
}