package com.example.friendquest.domain

data class Friend(
    val id: Int = 0,
    val name: String,
    val job: String?,
    val hobbies: String?,
    val favoriteFoods: String?,
    val foodsCannotEat: String?,
    val savedAiQuestions: List<String>? = null
)
