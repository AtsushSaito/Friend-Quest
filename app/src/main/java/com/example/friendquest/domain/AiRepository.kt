package com.example.friendquest.domain


interface AiRepository {
    suspend fun generateQuestions(friend: Friend): Result<List<String>>
}
