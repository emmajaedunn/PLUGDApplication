package com.example.plugd.viewmodels

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.plugd.model.Channel
import com.example.plugd.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    // --- FIX: ADDED CHANNEL LOGIC BACK ---
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels

    fun loadChannels() {
        firestore.collection("channels")
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Channel::class.java)?.copy(id = it.id)
                } ?: emptyList()
                _channels.value = list
            }
    }

    // --- MESSAGES LOGIC (Unchanged) ---
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadMessages(channelId: String) {
        firestore.collection("channels").document(channelId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toObject(Message::class.java)?.copy(id = it.id) } ?: emptyList()
                _messages.value = list
            }
    }

    suspend fun sendMessage(channelId: String, text: String, replyTo: Message? = null) {
        val user = auth.currentUser ?: return
        val msg = hashMapOf(
            "channelId" to channelId,
            "senderId" to user.uid,
            "senderName" to user.displayName,
            "senderProfileUrl" to user.photoUrl?.toString(),
            "content" to text,
            "timestamp" to System.currentTimeMillis(),
            "replyToMessageId" to replyTo?.id,
            "replyToSnippet" to replyTo?.content?.take(80),
        )
        firestore.collection("channels").document(channelId)
            .collection("messages").add(msg).await()
    }

    suspend fun sendAttachment(channelId: String, uri: Uri, contentResolver: ContentResolver) {
        val user = auth.currentUser ?: return
        val type = contentResolver.getType(uri) ?: "application/octet-stream"
        val fileName = UUID.randomUUID().toString()

        val ref = storage.reference.child("channels/$channelId/attachments/$fileName")
        ref.putFile(uri).await()
        val downloadUrl = ref.downloadUrl.await().toString()

        val msg = hashMapOf(
            "channelId" to channelId,
            "senderId" to user.uid,
            "senderName" to user.displayName,
            "senderProfileUrl" to user.photoUrl?.toString(),
            "content" to "",
            "timestamp" to System.currentTimeMillis(),
            "mediaUrl" to downloadUrl,
            "mediaType" to type
        )
        firestore.collection("channels").document(channelId)
            .collection("messages").add(msg).await()
    }

    suspend fun toggleReaction(channelId: String, messageId: String, emoji: String) {
        val uid = auth.currentUser?.uid ?: return
        val msgRef = firestore.collection("channels").document(channelId)
            .collection("messages").document(messageId)

        firestore.runTransaction { tx ->
            val snap = tx.get(msgRef)
            val reactions = (snap.get("reactions") as? Map<String, Long>)?.toMutableMap() ?: mutableMapOf()
            val reactors = (snap.get("reactors") as? Map<String, String>)?.toMutableMap() ?: mutableMapOf()

            val current = reactors[uid]
            if (current == emoji) {
                reactors.remove(uid)
                reactions[emoji] = (reactions[emoji] ?: 1) - 1
                if ((reactions[emoji] ?: 0) <= 0) reactions.remove(emoji)
            } else {
                if (current != null) {
                    reactions[current] = (reactions[current] ?: 1) - 1
                    if ((reactions[current] ?: 0) <= 0) reactions.remove(current)
                }
                reactors[uid] = emoji
                reactions[emoji] = (reactions[emoji] ?: 0) + 1
            }

            tx.update(msgRef, mapOf("reactions" to reactions, "reactors" to reactors))
            null
        }.await()
    }
}




























/*class ChatViewModel(
    private val repository: ChatRepository,
    val currentUserId: String
) : ViewModel() {

    // Channels
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> get() = _channels

    fun loadChannels() {
        viewModelScope.launch {
            repository.getChannels().collect { _channels.value = it }
        }
    }

    // Messages
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages

    fun loadMessages(channelId: String) {
        viewModelScope.launch {
            repository.getMessages(channelId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    // Observe Real-time Messages
    fun observeRealtimeMessages(channelId: String, onNewMessage: (Message) -> Unit) {
        viewModelScope.launch {
            repository.observeRealtimeMessages(channelId).collect { msg ->
                if (_messages.value.none { it.id == msg.id }) {
                    _messages.value = _messages.value + msg
                    if (msg.senderId != currentUserId) onNewMessage(msg)
                }
            }
        }
    }

    // Current User
    private val _currentUserName = mutableStateOf("Loading...")
    val currentUserName: State<String> get() = _currentUserName

    init {
        viewModelScope.launch {
            try {
                _currentUserName.value = repository.getUserName(currentUserId)
            } catch (e: Exception) {
                _currentUserName.value = "Unknown"
            }
        }
    }

    /* CORRECT  Send Message
    fun sendMessage(channelId: String, content: String) {
        viewModelScope.launch {
            try {
                // Use Firestore username
                val username = _currentUserName.value

                val message = Message(
                    id = "",
                    channelId = channelId,
                    senderId = currentUserId,
                    content = content,
                    senderName = username,
                    timestamp = System.currentTimeMillis()
                )

                repository.sendMessage(channelId, message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/

    fun sendMessage(channelId: String, content: String) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val username = repository.getUserName(userId) // fetch latest

                val message = Message(
                    id = "",
                    channelId = channelId,
                    senderId = userId,
                    content = content,
                    senderName = username,
                    timestamp = System.currentTimeMillis()
                )
                repository.sendMessage(channelId, message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}*/


