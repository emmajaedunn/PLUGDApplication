package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.ChatDao
import com.example.plugd.data.mappers.toMessage
import com.example.plugd.data.mappers.toMessageEntity
import com.example.plugd.model.Channel
import com.example.plugd.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val chatDao: ChatDao
) {

    // ---------------- CHANNELS ----------------
    fun getChannels(): Flow<List<Channel>> = callbackFlow {
        val listener = db.collection("channels")
            .addSnapshotListener { snapshot, _ ->
                val channels = snapshot?.documents?.map {
                    it.toObject(Channel::class.java)?.copy(id = it.id)
                }?.filterNotNull() ?: emptyList()
                trySend(channels)
            }
        awaitClose { listener.remove() }
    }

    // ---------------- MESSAGES ----------------
    fun getMessages(channelId: String): Flow<List<Message>> = callbackFlow {
        // 1️⃣ Emit cached Room messages first
        val cached = chatDao.getMessages(channelId).first().map { it.toMessage() }
        if (cached.isNotEmpty()) trySend(cached)

        // 2️⃣ Listen to Firestore for updates
        val listener = db.collection("channels")
            .document(channelId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.map {
                    it.toObject(Message::class.java)?.copy(id = it.id)
                }?.filterNotNull() ?: emptyList()

                // Send to Flow
                trySend(messages)

                // Save to Room for offline
                CoroutineScope(Dispatchers.IO).launch {
                    chatDao.insertMessages(messages.map { it.toMessageEntity() })
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun getLocalMessages(channelId: String): List<Message> {
        return chatDao.getMessages(channelId).first().map { it.toMessage() }
    }

    suspend fun getUserName(userId: String): String {
        val doc = db.collection("users").document(userId).get().await()
        // Fetch 'username' field instead of 'displayName'
        return doc.getString("username") ?: "Unknown"
    }

    // ---------------- REAL-TIME SINGLE MESSAGE OBSERVER ----------------
    fun observeRealtimeMessages(channelId: String): Flow<Message> = callbackFlow {
        val listener = db.collection("channels")
            .document(channelId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documentChanges?.forEach { change ->
                    val msg =
                        change.document.toObject(Message::class.java)?.copy(id = change.document.id)
                    msg?.let {
                        trySend(it)

                        // Save new message to Room
                        CoroutineScope(Dispatchers.IO).launch {
                            chatDao.insertMessage(it.toMessageEntity())
                        }
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(channelId: String, message: Message) {
        val docRef = db.collection("channels")
            .document(channelId)
            .collection("messages")
            .document() // creates a document with a Firestore ID

        val messageWithId = message.copy(id = docRef.id)

        // 1️⃣ Push to Firestore
        docRef.set(messageWithId)
            .await() // you may need kotlinx-coroutines-play-services for .await()

        // 2️⃣ Save to Room
        withContext(Dispatchers.IO) {
            chatDao.insertMessage(messageWithId.toMessageEntity())
        }
    }
}
