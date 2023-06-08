package com.bangkit.ecoease.ui.screen.chat

import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.data.firebase.FireBaseRealtimeDatabase
import com.bangkit.ecoease.data.firebase.FireBaseRealtimeDatabase.getCurrentChat
import com.bangkit.ecoease.data.model.Message
import com.bangkit.ecoease.data.model.request.FCMNotification
import com.bangkit.ecoease.data.model.request.Notification
import com.bangkit.ecoease.data.room.model.User
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.ChatBubble
import com.bangkit.ecoease.ui.component.ErrorHandler
import com.bangkit.ecoease.ui.component.TextInput
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.StateFlow
import java.util.*

@Composable
fun ChatRoomScreen(
    roomId: String,
    getCurrentUser: () -> Unit,
    reloadGetCurrentUser: () -> Unit,
    userUiState: StateFlow<UiState<User>>,
    sendNotification: (FCMNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messagesRef = FireBaseRealtimeDatabase.createMessageRef(roomId)
    var message: String by rememberSaveable { mutableStateOf("") }
    var chats: MutableList<Message> = remember { mutableStateListOf() }
    var loading by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    var username by remember { mutableStateOf("") }
    var token: String? by remember { mutableStateOf(null) }
    var users: MutableList<String> = remember {
        mutableStateListOf()
    }


    LaunchedEffect(Unit) {
        messagesRef.getCurrentChat(roomId = roomId).addOnCompleteListener {
            if (it.isSuccessful) {
                chats.clear()
                chats.addAll(it.result)
            }
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            token = it.result
        }
    }

    LaunchedEffect(chats.size) {
        if (chats.size != 0) {
            lazyListState.animateScrollToItem(chats.size)
            users = chats.map { it.token ?: "" }.toSet().toMutableStateList()
            users.map {

                Log.d("TAG", "ChatRoomScreen token user: ${it} $username")
            }
        }
    }

    DisposableEffect(Unit) {
        val childEventListener = FireBaseRealtimeDatabase.childEventListener { chats.add(it) }
        messagesRef.addChildEventListener(childEventListener)
        onDispose {
            messagesRef.removeEventListener(childEventListener)
        }
    }

    fun handleSendMessage() {
        try {
            messagesRef
                .push()
                .setValue(
                    Message(
                        token = token,
                        text = message,
                        name = username,
                        timeStamp = Date().time
                    )
                ) { error, _ -> if (error != null) throw Exception(error.message) }
            users.map { fcmToken ->

            }
            sendNotification(
                FCMNotification(
                    to = "cLhb5UK5QKmaIHBzdKYIPe:APA91bFAFPzhPc_T9rt0qHnJA28wCx8yyZPzCExws54blUrZ0071dbl-n8fu13Byd6VNa-VVASTKeYLv7g7nyjk4kWw0UPU9UpqT_tSFlD1q6NGdZJHB90omjwMZAWyxalTfIxfNNORa",
                    notification = Notification(
                        body = message,
                        title = username,
                        subTitle = message,
                    )
                )
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            message = ""
        }
    }

    fun setUsername(user: User){
        username = "${user.firstName} ${user.lastName}"
    }

    val animatedIconBgColor by animateColorAsState(
        targetValue = if (message.isNotEmpty()) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
        animationSpec = tween(200)
    )

    userUiState.collectAsState().value.let { uiState ->
        when (uiState) {
            is UiState.Loading -> {
                Loader(modifier = Modifier.fillMaxWidth())
                getCurrentUser()
            }
            is UiState.Success -> {
                setUsername(uiState.data)
                Column(modifier = modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f)) {
                        AnimatedVisibility(
                            visible = !loading,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 32.dp),
                                contentPadding = PaddingValues(bottom = 48.dp),
                                state = lazyListState
                            ) {
                                items(chats.toList()) { message ->
                                    ChatBubble(
                                        message = message.text ?: "",
                                        sender = message.name ?: "",
                                        isOwner = message.name == username,
                                        date = DateUtils.getRelativeTimeSpanString(
                                            message.timeStamp ?: 0
                                        )
                                            .toString()
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        TextInput(
                            placeHolder = "Type message",
                            value = message,
                            onValueChange = { it -> message = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 52.dp)
                        )
                        IconButton(
                            onClick = { handleSendMessage() }, modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(animatedIconBgColor)
                                .padding(8.dp)
                                .align(Alignment.CenterEnd),
                            enabled = message.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Send,
                                tint = Color.White,
                                contentDescription = "send message icon",
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                ErrorHandler(errorText = uiState.errorMessage, onReload = {
                    reloadGetCurrentUser()
                })
            }
        }
    }
}

@Composable
private fun Loader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}