package com.example.plugd.viewmodels

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.model.Channel
import com.example.plugd.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatViewModel(
    private val profileRepository: ProfileRepository,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels

    // --- helper to fetch username + profilePictureUrl from Firestore ---
    private suspend fun getCurrentUserMeta(): Pair<String?, String?> {
        val user = auth.currentUser ?: return null to null
        val snap = firestore.collection("users").document(user.uid).get().await()
        val username = snap.getString("username")
        val profileUrl = snap.getString("profilePictureUrl")
        return username to profileUrl
    }

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

    // Fetch username + profilePictureUrl from Firestore
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // Load messages for a channel
    fun loadMessages(channelId: String) {
        firestore.collection("channels").document(channelId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toObject(Message::class.java)?.copy(id = it.id) } ?: emptyList()
                _messages.value = list
            }
    }

    // Send message
    suspend fun sendMessage(channelId: String, text: String, replyTo: Message? = null) {
        val user = auth.currentUser ?: return
        val (username, profileUrl) = getCurrentUserMeta()

        val msg = hashMapOf(
            "channelId" to channelId,
            "senderId" to user.uid,
            "senderName" to username,
            "senderProfileUrl" to profileUrl,
            "content" to text,
            "timestamp" to System.currentTimeMillis(),
            "replyToMessageId" to replyTo?.id,
            "replyToSnippet" to replyTo?.content?.take(80),
        )

        firestore.collection("channels").document(channelId)
            .collection("messages").add(msg).await()

        if (replyTo != null && replyTo.senderId != user.uid) {
            val meProfile = profileRepository.getRemoteProfile(user.uid)
            val myUsername = meProfile?.username ?: username ?: "Someone"

            profileRepository.addMessageReplyActivity(
                toUserId = replyTo.senderId,
                fromUserId = user.uid,
                fromUsername = myUsername,
                chatId = channelId,
                messagePreview = text.take(40)
            )
        }
    }

    // Send attachment
    suspend fun sendAttachment(channelId: String, uri: Uri, contentResolver: ContentResolver) {
        val user = auth.currentUser ?: return
        val (username, profileUrl) = getCurrentUserMeta()

        // Detect type (e.g., image/jpeg)
        val type = contentResolver.getType(uri) ?: "application/octet-stream"
        val fileName = UUID.randomUUID().toString()

        val ref = storage.reference.child("channels/$channelId/attachments/$fileName")

        // Upload to Storage
        ref.putFile(uri).await()
        val downloadUrl = ref.downloadUrl.await().toString()

        // Build message with media fields
        val msg = hashMapOf(
            "channelId" to channelId,
            "senderId" to user.uid,
            "senderName" to username,
            "senderProfileUrl" to profileUrl,
            "content" to "", // attachment-only message
            "timestamp" to System.currentTimeMillis(),
            "mediaUrl" to downloadUrl,
            "mediaType" to type
        )

        // Save to Firestore
        firestore.collection("channels").document(channelId)
            .collection("messages")
            .add(msg)
            .await()
    }

    // Send reply
    fun sendReply(chatId: String, toUserId: String, text: String) {
        viewModelScope.launch {

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val me = profileRepository.getRemoteProfile(currentUserId)
            val myUsername = me?.username ?: "Someone"

            profileRepository.addMessageReplyActivity(
                toUserId = toUserId,
                fromUserId = currentUserId,
                fromUsername = myUsername,
                chatId = chatId,
                messagePreview = text.take(40)
            )
        }
    }

    // Toggle reaction
    suspend fun toggleReaction(channelId: String, messageId: String, emoji: String) {
        val uid = auth.currentUser?.uid ?: return
        val msgRef = firestore.collection("channels").document(channelId)
            .collection("messages").document(messageId)

        // Run the reaction transaction
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

        // Update activity feed
        val snap = msgRef.get().await()
        val ownerId = snap.getString("senderId") ?: return
        if (ownerId == uid) return

        val meProfile = profileRepository.getRemoteProfile(uid)
        val myUsername = meProfile?.username ?: "Someone"

        profileRepository.addActivity(
            userId = ownerId,
            type = "reaction",
            fromUserId = uid,
            fromUsername = myUsername,
            message = "reacted $emoji to your message",
            postId = messageId
        )
    }
}