package com.example.friendquest.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friend: FriendEntity)

    @Update
    suspend fun update(friend: FriendEntity)

    @Delete
    suspend fun delete(friend: FriendEntity)

    @Query("SELECT * FROM friends WHERE id = :id")
    suspend fun getFriendById(id: Int): FriendEntity?

    @Query("SELECT * FROM friends ORDER BY name ASC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Query("SELECT COUNT(*) FROM friends")
    suspend fun getFriendCount(): Int
}
