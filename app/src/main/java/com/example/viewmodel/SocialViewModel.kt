package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SocialRepository
import com.example.data.Friendship
import com.example.data.Message
import com.example.data.CreatorProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SocialViewModel(private val repository: SocialRepository) : ViewModel() {

    private val _friendships = MutableStateFlow<List<Friendship>>(emptyList())
    val friendships: StateFlow<List<Friendship>> = _friendships.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeFriendships().collect { _friendships.value = it }
        }
    }

    fun sendFriendRequest(targetUserId: String) {
        viewModelScope.launch {
            repository.sendFriendRequest(targetUserId)
        }
    }

    fun acceptFriendRequest(friendshipId: String) {
        viewModelScope.launch {
            repository.acceptFriendRequest(friendshipId)
        }
    }
}
