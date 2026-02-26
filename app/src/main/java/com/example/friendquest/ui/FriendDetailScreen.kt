package com.example.friendquest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // For AI questions dialog
import androidx.compose.foundation.lazy.items // For AI questions dialog
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.friendquest.domain.Friend
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendDetailScreen(
    friend: Friend,
    aiQuestionsForDialog: List<String>,
    isLoadingAiQuestions: Boolean, // New parameter
    aiQuestionsError: String?,    // New parameter
    onNavigateUp: () -> Unit,
    onEditClicked: (Friend) -> Unit,
    onDeleteClicked: (Friend) -> Unit,
    onGenerateAiQuestions: (Friend) -> Unit,
    onClearAiQuestionsDialog: () -> Unit,
    onClearAiError: () -> Unit,      // New parameter
    onSaveAiQuestions: suspend (Int, List<String>) -> Unit,
    onDeleteSavedQuestion: suspend (Int, String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    // isLoadingAiQuestions is now used to control dialog display if questions are being fetched
    val showAiQuestionsDialog = aiQuestionsForDialog.isNotEmpty() && !isLoadingAiQuestions
    val selectedAiQuestionsState = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Friend") },
            text = { Text("Are you sure you want to delete ${friend.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClicked(friend)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Only show AI questions dialog if not loading and questions are available
    if (showAiQuestionsDialog) {
        AlertDialog(
            onDismissRequest = {
                selectedAiQuestionsState.clear()
                onClearAiQuestionsDialog()
            },
            title = { Text("Conversation Starters") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(aiQuestionsForDialog) { question ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedAiQuestionsState.contains(question),
                                onCheckedChange = {
                                    if (selectedAiQuestionsState.contains(question)) {
                                        selectedAiQuestionsState.remove(question)
                                    } else {
                                        selectedAiQuestionsState.add(question)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(question, modifier = Modifier.weight(1f))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            onSaveAiQuestions(friend.id, selectedAiQuestionsState.toList())
                            selectedAiQuestionsState.clear()
                            onClearAiQuestionsDialog()
                        }
                    }
                ) {
                    Text("Save Selected")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedAiQuestionsState.clear()
                    onClearAiQuestionsDialog()
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(friend.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClicked(friend) }) {
                        Icon(Icons.Filled.Edit, "Edit Friend")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, "Delete Friend")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it) // Padding from Scaffold
                .padding(16.dp) // Additional padding for content
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                DetailItem(label = "名前", value = friend.name)
                friend.job?.let { job -> DetailItem(label = "職業", value = job) }
                friend.hobbies?.let { hobbies -> DetailItem(label = "趣味", value = hobbies) }
                friend.favoriteFoods?.let { foods -> DetailItem(label = "好きな食べ物", value = foods) }
                friend.foodsCannotEat?.let { foods -> DetailItem(label = "嫌いな食べ物", value = foods) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { 
                    if (!isLoadingAiQuestions) {
                        onClearAiError() // Clear previous error before new request
                        onGenerateAiQuestions(friend) 
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoadingAiQuestions // Disable button while loading
            ) {
                if (isLoadingAiQuestions) {
                    CircularProgressIndicator(modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Generating...")
                } else {
                    Icon(Icons.Filled.Info, contentDescription = "質問を生成する", modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("質問を生成する")
                }
            }

            aiQuestionsError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth().clickable { onClearAiError() }
                )
            }

            friend.savedAiQuestions?.takeIf { questions -> questions.isNotEmpty() }?.let {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Saved Conversation Starters:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    it.forEach { question ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("- $question", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                scope.launch {
                                    onDeleteSavedQuestion(friend.id, question)
                                }
                            }) {
                                Icon(Icons.Filled.Close, contentDescription = "Delete question: $question", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendDetailScreen() {
    MaterialTheme {
        val sampleFriend = Friend(
            id = 1,
            name = "Charly Day",
            job = "Actor, Writer, Producer, Podcaster",
            hobbies = "Music (piano, guitar, drums), Writing songs, Playing baseball, Dayman karate",
            favoriteFoods = "Milk steak (boiled over hard with a side of your finest jelly beans, raw), Rum ham, Riot Juice, Grilled Charlies",
            foodsCannotEat = "Spiders, The Nightman Cometh script (ate it)",
            savedAiQuestions = listOf("What's new with the podcast?", "Any interesting music lately?")
        )
        FriendDetailScreen(
            friend = sampleFriend,
            aiQuestionsForDialog = listOf("Mock question 1?", "Mock question 2?", "How is acting?"),
            isLoadingAiQuestions = false,
            aiQuestionsError = null,
            onNavigateUp = {},
            onEditClicked = {},
            onDeleteClicked = {},
            onGenerateAiQuestions = {},
            onClearAiQuestionsDialog = {},
            onClearAiError = {},
            onSaveAiQuestions = { _, _ -> },
            onDeleteSavedQuestion = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendDetailScreenLoading() { // Preview for loading state
    MaterialTheme {
        val sampleFriend = Friend(id = 1, name = "Charly Day", job = "Actor, Writer, Producer, Podcaster",hobbies = "Music (piano, guitar, drums), Writing songs, Playing baseball, Dayman karate", favoriteFoods = "Milk steak (boiled over hard with a side of your finest jelly beans, raw), Rum ham, Riot Juice, Grilled Charlies",foodsCannotEat = "Spiders, The Nightman Cometh script (ate it)")
        FriendDetailScreen(
            friend = sampleFriend,
            aiQuestionsForDialog = emptyList(),
            isLoadingAiQuestions = true,
            aiQuestionsError = null,
            onNavigateUp = {},
            onEditClicked = {},
            onDeleteClicked = {},
            onGenerateAiQuestions = {},
            onClearAiQuestionsDialog = {},
            onClearAiError = {},
            onSaveAiQuestions = { _, _ -> },
            onDeleteSavedQuestion = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendDetailScreenWithError() { // Preview for error state
    MaterialTheme {
        val sampleFriend = Friend(id = 1, name = "Charly Day", job = "Actor, Writer, Producer, Podcaster",hobbies = "Music (piano, guitar, drums), Writing songs, Playing baseball, Dayman karate", favoriteFoods = "Milk steak (boiled over hard with a side of your finest jelly beans, raw), Rum ham, Riot Juice, Grilled Charlies",foodsCannotEat = "Spiders, The Nightman Cometh script (ate it)")
        FriendDetailScreen(
            friend = sampleFriend,
            aiQuestionsForDialog = emptyList(),
            isLoadingAiQuestions = false,
            aiQuestionsError = "Failed to generate questions. Please try again.",
            onNavigateUp = {},
            onEditClicked = {},
            onDeleteClicked = {},
            onGenerateAiQuestions = {},
            onClearAiQuestionsDialog = {},
            onClearAiError = {},
            onSaveAiQuestions = { _, _ -> },
            onDeleteSavedQuestion = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendDetailScreenNoSavedQuestions() {
    MaterialTheme {
        val sampleFriend = Friend(
            id = 2,
            name = "Dee Reynolds",
            job = "Waitress, Aspiring Actress",
            hobbies = "Improv, Avoiding Charly",
            favoriteFoods = "Wine",
            foodsCannotEat = "Crackers (too dry)",
            savedAiQuestions = null
        )
        FriendDetailScreen(
            friend = sampleFriend,
            aiQuestionsForDialog = emptyList(),
            isLoadingAiQuestions = false,
            aiQuestionsError = null,
            onNavigateUp = {},
            onEditClicked = {},
            onDeleteClicked = {},
            onGenerateAiQuestions = {},
            onClearAiQuestionsDialog = {},
            onClearAiError = {},
            onSaveAiQuestions = { _, _ -> },
            onDeleteSavedQuestion = { _, _ -> }
        )
    }
}
