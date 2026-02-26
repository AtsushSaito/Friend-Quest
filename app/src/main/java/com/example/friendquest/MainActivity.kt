package com.example.friendquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.friendquest.data.local.AppDatabase
import com.example.friendquest.data.local.LocalFriendRepository
import com.example.friendquest.domain.Friend
import com.example.friendquest.ui.AddEditFriendScreen
import com.example.friendquest.ui.FriendDetailScreen
import com.example.friendquest.ui.FriendListScreen
import com.example.friendquest.ui.theme.FriendQuestTheme
import com.example.friendquest.viewmodel.FriendViewModel
import com.example.friendquest.viewmodel.FriendViewModelFactory
import com.example.friendquest.data.remote.GeminiRepository

object Routes {
    const val FRIEND_LIST = "friendList"
    const val ADD_EDIT_FRIEND = "addEditFriend"
    const val FRIEND_DETAIL = "friendDetail/{friendId}"

    fun addEditFriendRoute(friendId: Int?) = if (friendId != null) "$ADD_EDIT_FRIEND?friendId=$friendId" else ADD_EDIT_FRIEND
    fun friendDetailRoute(friendId: Int) = "friendDetail/$friendId"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FriendQuestTheme {
                val context = LocalContext.current
                val db = AppDatabase.getDatabase(context)
                val friendDao = db.friendDao()
                val friendRepository = LocalFriendRepository(friendDao)
                val geminiRepository = GeminiRepository(context)

                val viewModelFactory = FriendViewModelFactory(friendRepository, geminiRepository)
                val friendViewModel: FriendViewModel = viewModel(factory = viewModelFactory)

                AppNavigation(friendViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: FriendViewModel) {
    val navController = rememberNavController()
    val aiQuestionsForDialog by viewModel.aiQuestionsForDialog.collectAsState()
    val isLoadingAiQuestions by viewModel.isLoadingAiQuestions.collectAsState()
    val aiQuestionsError by viewModel.aiQuestionsError.collectAsState()

    NavHost(navController = navController, startDestination = Routes.FRIEND_LIST) {
        composable(Routes.FRIEND_LIST) {
            val friends by viewModel.allFriends.collectAsState()
            FriendListScreen(
                friends = friends,
                onAddFriendClicked = {
                    navController.navigate(Routes.addEditFriendRoute(null))
                },
                onFriendClicked = { friend ->
                    navController.navigate(Routes.friendDetailRoute(friend.id))
                }
            )
        }

        composable(
            route = Routes.ADD_EDIT_FRIEND + "?friendId={friendId}",
            arguments = listOf(navArgument("friendId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getInt("friendId")
            var friendToEdit: Friend? by remember { mutableStateOf(null) }
            var isLoadingData by remember { mutableStateOf(false) }

            if (friendId != null && friendId != -1) {
                LaunchedEffect(friendId) {
                    isLoadingData = true
                    friendToEdit = viewModel.getFriendById(friendId)
                    isLoadingData = false
                }
            }
            
            if (!(friendId != null && friendId != -1 && isLoadingData)) {
                 AddEditFriendScreen(
                    friend = if (friendId == -1) null else friendToEdit,
                    onSaveClicked = { friendDetails ->
                        if (friendDetails.id == 0) { 
                            viewModel.addFriend(friendDetails)
                        } else { 
                            viewModel.updateFriend(friendDetails)
                        }
                        navController.popBackStack(Routes.FRIEND_LIST, inclusive = false)
                    },
                    onCancelClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = Routes.FRIEND_DETAIL,
            arguments = listOf(navArgument("friendId") { type = NavType.IntType })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getInt("friendId")
            var friend: Friend? by remember { mutableStateOf(null) }
            var isLoadingData by remember { mutableStateOf(true) }

            LaunchedEffect(friendId) {
                if (friendId != null) {
                    isLoadingData = true
                    friend = viewModel.getFriendById(friendId)
                    isLoadingData = false
                }
            }

            if (isLoadingData) {
                // Loading indicator can be shown here
            } else if (friend != null) {
                FriendDetailScreen(
                    friend = friend!!,
                    aiQuestionsForDialog = aiQuestionsForDialog,
                    isLoadingAiQuestions = isLoadingAiQuestions, 
                    aiQuestionsError = aiQuestionsError,         
                    onNavigateUp = { navController.popBackStack() },
                    onEditClicked = { f ->
                        navController.navigate(Routes.addEditFriendRoute(f.id))
                    },
                    onDeleteClicked = { f ->
                        viewModel.deleteFriend(f)
                        navController.popBackStack()
                    },
                    onGenerateAiQuestions = viewModel::generateAiQuestionsForFriend,
                    onClearAiQuestionsDialog = viewModel::clearAiQuestionsDialog,
                    onClearAiError = viewModel::clearAiError, 
                    onSaveAiQuestions = { id, questions ->
                        viewModel.saveAiQuestionsToFriend(id, questions)
                        if (friendId != null) {
                            friend = viewModel.getFriendById(friendId)
                        }
                    },
                    onDeleteSavedQuestion = { id, question ->
                        viewModel.deleteSavedAiQuestion(id, question)
                        if (friendId != null) {
                            friend = viewModel.getFriendById(friendId)
                        }
                    }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() } 
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FriendQuestTheme {
        Text("App Preview - Run on device/emulator to see full navigation with ViewModel")
    }
}
