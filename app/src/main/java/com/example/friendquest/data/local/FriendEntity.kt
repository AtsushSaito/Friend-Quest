package com.example.friendquest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.friendquest.domain.Friend

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val job: String?,
    val hobbies: String?,
    val favoriteFoods: String?,
    val foodsCannotEat: String?,
    val savedAiQuestions: List<String>? = null
) {
    fun toDomain() = Friend(id, name, job, hobbies, favoriteFoods, foodsCannotEat, savedAiQuestions)
}

fun Friend.toEntity() = FriendEntity(id, name, job, hobbies, favoriteFoods, foodsCannotEat, savedAiQuestions)
