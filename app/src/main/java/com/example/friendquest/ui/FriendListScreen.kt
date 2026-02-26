package com.example.friendquest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
// import androidx.compose.foundation.layout.Spacer // Keep Spacer if needed for layout (Removed as not directly used in the provided snippet for FriendListScreen)
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.layout.width // Keep width if needed for Spacer (Removed as not directly used in the provided snippet for FriendListScreen)
import androidx.compose.foundation.lazy.grid.GridCells // Added for LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // Added for LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // Changed from foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CardDefaults // Added for Card colors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// import androidx.compose.ui.graphics.Color // Removed as it's no longer used directly for card color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.friendquest.domain.Friend // Import your Friend model

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(
    friends: List<Friend>,
    onAddFriendClicked: () -> Unit,
    onFriendClicked: (Friend) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("友達一覧") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFriendClicked) {
                Icon(Icons.Filled.Add, "Add new friend")
            }
        }
    ) { paddingValues ->
        if (friends.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No friends yet. Tap the '+' button to add your first friend!")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Defines a grid with 2 columns
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Adds horizontal spacing between items
            ) {
                items(friends, key = { friend -> friend.id }) { friend ->
                    FriendListItem(friend = friend, onClick = { onFriendClicked(friend) })
                }
            }
        }
    }
}

@Composable
fun FriendListItem(
    friend: Friend,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(), // In a grid, fillMaxWidth might make items too wide if not constrained by the grid cell.
                           // Consider if this is the desired behavior or if items should wrap content.
                           // For now, keeping it as is.
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer // Matched to TopAppBar color
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = friend.name, style = MaterialTheme.typography.titleMedium)
                friend.job?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendListScreenEmpty() {
    MaterialTheme {
        FriendListScreen(
            friends = emptyList(),
            onAddFriendClicked = {},
            onFriendClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendListScreenWithData() {
    MaterialTheme {
        val sampleFriends = listOf(
            Friend(1, "Alice", "Developer", "Coding", "Pizza", "Peanuts"),
            Friend(2, "Bob", "Designer", "Drawing", "Sushi", "Shellfish"),
            Friend(3, "Carol", "Manager", "Meetings", "Salad", "Gluten"),
            Friend(4, "Dave", "Tester", "Testing", "Coffee", "Dairy")
        )
        FriendListScreen(
            friends = sampleFriends,
            onAddFriendClicked = {},
            onFriendClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendListItem() {
    MaterialTheme {
        FriendListItem(
            friend = Friend(1, "Charly Day", "Actor", "Music, Writing", "Milk Steak", "Spiders"),
            onClick = {}
        )
    }
}
