package com.example.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.snapshots
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SocialRepository(private val db: FirebaseFirestore, private val auth: com.google.firebase.auth.FirebaseAuth) {

    private fun requireUserId(): String = auth.currentUser?.uid
        ?: throw IllegalStateException("User must be signed in")

    // User Search
    fun searchUsers(query: String): Flow<List<CreatorProfile>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users")
                .whereGreaterThanOrEqualTo("username", query)
                .whereLessThanOrEqualTo("username", query + "\uf8ff")
                .snapshots()
                .map { snapshot -> 
                    snapshot.toObjects(CreatorProfile::class.java).filter { it.username.contains(query, ignoreCase = true) }
                }
        )
    }

    // Friendships
    fun observeFriendships(): Flow<List<Friendship>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("friendships")
                .whereArrayContains("users", uid)
                .snapshots()
                .map { it.toObjects(Friendship::class.java) }
        )
    }

    suspend fun sendFriendRequest(targetUserId: String) {
        val uid = requireUserId()
        val friendship = Friendship(
            users = listOf(uid, targetUserId).sorted(),
            status = "pending",
            requestSender = uid,
            createdAt = Timestamp.now()
        )
        db.collection("friendships").add(friendship).await()
    }

    suspend fun acceptFriendRequest(friendshipId: String) {
        db.collection("friendships").document(friendshipId)
            .update("status", "accepted").await()
    }

    // Messaging
    fun observeMessages(chatId: String): Flow<List<Message>> = flow {
        emitAll(
            db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp")
                .snapshots()
                .map { it.toObjects(Message::class.java) }
        )
    }

    suspend fun sendMessage(chatId: String, text: String) {
        val uid = requireUserId()
        val message = Message(
            senderId = uid,
            text = text,
            timestamp = Timestamp.now()
        )
        db.collection("chats").document(chatId).collection("messages").add(message).await()
        db.collection("chats").document(chatId).update("lastMessageText", text, "updatedAt", Timestamp.now()).await()
    }
    
    // Status
    suspend fun updateOnlineStatus(isOnline: Boolean) {
        val uid = requireUserId()
        db.collection("users").document(uid).update("onlineStatus", isOnline, "lastSeen", Timestamp.now()).await()
    }
}
