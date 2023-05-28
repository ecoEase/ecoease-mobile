package com.bangkit.ecoease.ui.screen.chat

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.firebase.FireBaseRealtimeDatabase
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.component.Avatar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersChatsScreen(
    navHostController: NavHostController,
    onLoadChatRooms: () -> Unit,
    modifier: Modifier = Modifier
){
    var loading by remember{
        mutableStateOf(false)
    }
    var rooms: List<String?> by remember{
        mutableStateOf(listOf())
    } 
    LaunchedEffect(Unit){
        loading = true
        val reference = FireBaseRealtimeDatabase.getAllRoomsKey()
        reference.addOnCompleteListener{
            loading = false
            if(it.isSuccessful){
                Log.d("UsersChat", "UsersChatsScreen: ${ it.result}")
                rooms = it.result
            }
        }
    }

    val refreshState = rememberCoroutineScope()
    var refreshing: Boolean by remember{ mutableStateOf(false) }

    fun refresh() = refreshState.launch{
        refreshing = true
        delay(200)
        onLoadChatRooms()
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)){
        Column(modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)) {
            if(loading) CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            AnimatedVisibility(visible = !loading, modifier = Modifier
                .weight(1f)
                .fillMaxWidth()) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)){
                    items(rooms.toList(), key = {it ?: generateUUID() }){roomId ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    roomId?.let { id ->
                                        Screen.ChatRoom.setTitle(id)
                                        val roomChatRoute = Screen.ChatRoom.createRoute(id)
                                        navHostController.navigate(roomChatRoute)
                                    }
                                },
                            horizontalArrangement = Arrangement.spacedBy(16.dp))
                        {
                            Avatar(imageUrl = "https://images.unsplash.com/photo-1596815064285-45ed8a9c0463?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=366&q=80")
                            Text(text = roomId ?: "")
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(refreshing = refreshing, state = pullRefreshState)
    }
}