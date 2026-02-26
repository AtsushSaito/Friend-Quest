package com.example.friendquest.domain

import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getAllFriends(): Flow<List<Friend>>
    suspend fun insert(friend: Friend)
    suspend fun update(friend: Friend)
    suspend fun delete(friend: Friend)
    suspend fun getFriendById(id: Int): Friend?
}
