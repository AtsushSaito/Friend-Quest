package com.example.friendquest.data.local

import com.example.friendquest.domain.Friend
import com.example.friendquest.domain.FriendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalFriendRepository(private val dao: FriendDao) : FriendRepository {
    override fun getAllFriends(): Flow<List<Friend>> =
        dao.getAllFriends().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insert(friend: Friend) = dao.insert(friend.toEntity())
    override suspend fun update(friend: Friend) = dao.update(friend.toEntity())
    override suspend fun delete(friend: Friend) = dao.delete(friend.toEntity())
    override suspend fun getFriendById(id: Int): Friend? = dao.getFriendById(id)?.toDomain()
}
