package com.bangkit.ecoease.ui.screen.chat

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.event.MyEvent
import com.bangkit.ecoease.data.firebase.FireBaseRealtimeDatabase
import com.bangkit.ecoease.data.firebase.FireBaseRealtimeDatabase.getAllRoomsKey
import com.bangkit.ecoease.data.model.Chatroom
import com.bangkit.ecoease.data.remote.responseModel.chatroom.ChatRoomItem
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.Avatar
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


private val gsonPretty = GsonBuilder().setPrettyPrinting().create()
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersChatsScreen(
    navHostController: NavHostController,
    onLoadChatRooms: () -> Unit,
    chatroomsUiState: StateFlow<UiState<List<ChatRoomItem>>>,
    onDeleteRoom: (id: String, key: String) -> Unit,
    eventFlow: Flow<MyEvent>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    var rooms: MutableList<Chatroom> = remember { mutableStateListOf() }
    val chatroomRef = FireBaseRealtimeDatabase.createRoomRef()
    LaunchedEffect(Unit) {
        loading = true
        chatroomRef.getAllRoomsKey().addOnCompleteListener {
            loading = false
            if (it.isSuccessful) {
                rooms.clear()
                rooms.addAll(it.result.toMutableList())
            }
        }
        eventFlow.collect { event ->
            when (event) {
                is MyEvent.MessageEvent -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    DisposableEffect(Unit) {
        val childEventListener =
            FireBaseRealtimeDatabase.roomChildEventListener(
                onChildAdded = { newRoom ->
                    rooms.add(newRoom)
                },
                onChildRemoved = { deletedRom ->
                    rooms.remove(deletedRom)
                },
            )
        chatroomRef.addChildEventListener(childEventListener)
        onDispose {
            chatroomRef.removeEventListener(childEventListener)
        }
    }

    val refreshState = rememberCoroutineScope()
    var refreshing: Boolean by remember { mutableStateOf(false) }

    fun refresh() = refreshState.launch {
        refreshing = true
        delay(200)
        onLoadChatRooms()
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            chatroomsUiState.collectAsState(initial = UiState.Loading).value.let {uiState ->
                when(uiState){
                    is UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        onLoadChatRooms()
                    }
                    is UiState.Success -> {
                        Log.d("TAG", "UsersChatsScreen: ${gsonPretty.toJson(uiState)}")
                        AnimatedVisibility(
                            visible = !loading, modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(uiState.data.toList(), key = { it.id ?: generateUUID() }) { room ->
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    room?.let {
                                                        Screen.ChatRoom.setTitle("${room.mitra.firstName} ${room.mitra.lastName}")
                                                        val roomChatRoute =
                                                            Screen.ChatRoom.createRoute(it.id ?: "")
                                                        navHostController.navigate(roomChatRoute)
                                                    }
                                                },
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        )
                                        {
                                            Avatar(imageUrl = room.mitra.urlPhotoProfile)
                                            Text(text = "${room.mitra.firstName} ${room.mitra.lastName}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text(uiState.errorMessage)
                    }
                }
            }
        }
        PullRefreshIndicator(refreshing = refreshing, state = pullRefreshState)
    }
}