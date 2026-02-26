package com.example.friendquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.friendquest.domain.AiRepository
import com.example.friendquest.domain.FriendRepository
import com.example.friendquest.domain.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FriendViewModel(
    private val friendRepository: FriendRepository,
    private val geminiRepository: AiRepository,
) : ViewModel() {

    val allFriends: StateFlow<List<Friend>> = friendRepository.getAllFriends()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _aiQuestionsForDialog = MutableStateFlow<List<String>>(emptyList())
    val aiQuestionsForDialog: StateFlow<List<String>> = _aiQuestionsForDialog.asStateFlow()

    private val _isLoadingAiQuestions = MutableStateFlow(false)
    val isLoadingAiQuestions: StateFlow<Boolean> = _isLoadingAiQuestions.asStateFlow()

    private val _aiQuestionsError = MutableStateFlow<String?>(null)
    val aiQuestionsError: StateFlow<String?> = _aiQuestionsError.asStateFlow()


    fun generateAiQuestionsForFriend(friend: Friend) {
        viewModelScope.launch {
            _isLoadingAiQuestions.value = true
            _aiQuestionsError.value = null
            _aiQuestionsForDialog.value = emptyList()

            // ViewModel のロジックを Repository に委譲
            geminiRepository.generateQuestions(friend)
                .onSuccess { _aiQuestionsForDialog.value = it }
                .onFailure { _aiQuestionsError.value = "質問の生成に失敗しました: ${it.localizedMessage}" }

            _isLoadingAiQuestions.value = false
        }
    }



    fun clearAiQuestionsDialog() {
        _aiQuestionsForDialog.value = emptyList()
    }

    fun clearAiError() {
        _aiQuestionsError.value = null
    }

    suspend fun saveAiQuestionsToFriend(friendId: Int, questionsToAdd: List<String>) {
        val friend = friendRepository.getFriendById(friendId)
        friend?.let {
            val existingQuestions = it.savedAiQuestions?.toMutableList() ?: mutableListOf()
            questionsToAdd.forEach { newQuestion ->
                if (!existingQuestions.contains(newQuestion)) {
                    existingQuestions.add(newQuestion)
                }
            }
            val updatedFriend = it.copy(savedAiQuestions = existingQuestions.toList())
            friendRepository.update(updatedFriend)
        }
    }

    suspend fun deleteSavedAiQuestion(friendId: Int, questionToDelete: String) {
        val friend = friendRepository.getFriendById(friendId)
        friend?.let {
            val existingQuestions = it.savedAiQuestions?.toMutableList() ?: mutableListOf()
            existingQuestions.remove(questionToDelete)
            val updatedFriend = it.copy(savedAiQuestions = existingQuestions.toList())
            friendRepository.update(updatedFriend)
        }
    }

    fun addFriend(friend: Friend) {
        viewModelScope.launch {
            friendRepository.insert(friend)
        }
    }

    fun updateFriend(friend: Friend) {
        viewModelScope.launch {
            val currentFriendDetails = friendRepository.getFriendById(friend.id)
            val updatedFriend = friend.copy(savedAiQuestions = currentFriendDetails?.savedAiQuestions)
            friendRepository.update(updatedFriend)
        }
    }

    fun deleteFriend(friend: Friend) {
        viewModelScope.launch {
            friendRepository.delete(friend)
        }
    }

    suspend fun getFriendById(id: Int): Friend? {
        return friendRepository.getFriendById(id)
    }
}

class FriendViewModelFactory(
    private val friendRepository: FriendRepository,
    private val geminiRepository: AiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendViewModel(friendRepository, geminiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}