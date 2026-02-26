package com.example.friendquest.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.friendquest.domain.Friend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFriendScreen(
    friend: Friend? = null,
    onSaveClicked: (Friend) -> Unit,
    onCancelClicked: () -> Unit
) {
    var name by remember { mutableStateOf(friend?.name ?: "") }
    var job by remember { mutableStateOf(friend?.job ?: "") }
    var hobbies by remember { mutableStateOf(friend?.hobbies ?: "") }
    var favoriteFoods by remember { mutableStateOf(friend?.favoriteFoods ?: "") }
    var foodsCannotEat by remember { mutableStateOf(friend?.foodsCannotEat ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (friend == null) "Add Friend" else "Edit Friend") },
                navigationIcon = {
                    IconButton(onClick = onCancelClicked) {
                        Icon(Icons.Filled.ArrowBack, "Back")
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
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("名前*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = job,
                onValueChange = { job = it },
                label = { Text("職業") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = hobbies,
                onValueChange = { hobbies = it },
                label = { Text("趣味") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = favoriteFoods,
                onValueChange = { favoriteFoods = it },
                label = { Text("好きな食べ物") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = foodsCannotEat,
                onValueChange = { foodsCannotEat = it },
                label = { Text("嫌いな食べ物") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancelClicked) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val friendDetails = Friend(
                                id = friend?.id ?: 0,
                                name = name.trim(),
                                job = job.trim().takeIf { it.isNotEmpty() },
                                hobbies = hobbies.trim().takeIf { it.isNotEmpty() },
                                favoriteFoods = favoriteFoods.trim().takeIf { it.isNotEmpty() },
                                foodsCannotEat = foodsCannotEat.trim().takeIf { it.isNotEmpty() }
                            )
                            onSaveClicked(friendDetails)
                        }
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddFriendScreen() {
    MaterialTheme {
        AddEditFriendScreen(
            onSaveClicked = { _ -> },
            onCancelClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditFriendScreen() {
    MaterialTheme {
        AddEditFriendScreen(
            friend = Friend(id = 1, name = "Jane Doe", job = "Engineer",hobbies = "Reading", favoriteFoods = "Pizza", foodsCannotEat = "running"),
            onSaveClicked = { _ -> },
            onCancelClicked = {}
        )
    }
}
