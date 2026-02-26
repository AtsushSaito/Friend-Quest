package com.example.friendquest.data.remote

import com.example.friendquest.domain.AiRepository

import android.content.Context
import android.util.Log
import com.example.friendquest.BuildConfig
import com.example.friendquest.domain.Friend
import com.google.ai.client.generativeai.GenerativeModel
import java.io.IOException

class GeminiRepository(private val context: Context) : AiRepository {

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = BuildConfig.GEMINI_MODEL_NAME,
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    override suspend fun generateQuestions(friend: Friend): Result<List<String>> {
        return try {
            val prompt = createPrompt(friend) ?: return Result.failure(Exception("プロンプト読み込み失敗"))
            val response = generativeModel.generateContent(prompt)
            val questions = parseResponse(response.text ?: "")
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createPrompt(friend: Friend): String? {
        return try {
            val template = context.assets.open("prompts/generate_questions_prompt_template.txt")
                .bufferedReader().use { it.readText() }
            var prompt = template.replace("{FRIEND_NAME}", friend.name)
            prompt = prompt.replace("{FRIEND_JOB_INFO}", friend.job?.let { "- ${friend.name}の仕事: $it" } ?: "")
            prompt = prompt.replace("{FRIEND_HOBBIES_INFO}", friend.hobbies?.let { "- ${friend.name}の趣味: $it" } ?: "")
            prompt = prompt.replace("{FRIEND_FAVORITE_FOODS_INFO}", friend.favoriteFoods?.let { "- ${friend.name}の好きな食べ物: $it" } ?: "")
            prompt = prompt.replace("{FRIEND_FOODS_CANNOT_EAT_INFO}", friend.foodsCannotEat?.let { "- ${friend.name}が食べられないもの: $it" } ?: "")
            prompt.lines().filter { it.trim().isNotEmpty() }.joinToString("\n")
        } catch (e: IOException) {
            Log.e("GeminiRepository", "Error loading prompt", e)
            null
        }
    }

    private fun parseResponse(text: String): List<String> {
        return text.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && (it.endsWith("?") || it.endsWith("？")) }
            .map { it.removePrefix("-").trim() }
            .map { it.removePrefix("・").trim() }
            .take(5)
    }
}
