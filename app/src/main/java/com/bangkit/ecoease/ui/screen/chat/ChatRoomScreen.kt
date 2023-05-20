package com.bangkit.ecoease.ui.screen.chat

import android.text.format.DateUtils
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bangkit.ecoease.data.firebase.FireBaseRealtimeDatabase
import com.bangkit.ecoease.data.firebase.FireBaseRealtimeDatabase.getCurrentChat
import com.bangkit.ecoease.data.model.Message
import com.bangkit.ecoease.ui.component.ChatBubble
import com.bangkit.ecoease.ui.component.TextInput
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import java.util.*

@Composable
fun ChatRoomScreen(
    roomId: String,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val messagesRef = FireBaseRealtimeDatabase.createMessageRef(roomId)
    var message: String by rememberSaveable { mutableStateOf("") }
    var chats: MutableList<Message> = remember{ mutableStateListOf() }
    var loading by remember{ mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    // TODO: move chat bussiness logic in a viewmodel
    LaunchedEffect(Unit){
        loading = true
        messagesRef.getCurrentChat(roomId = roomId).addOnCompleteListener {
            loading = false
            if(it.isSuccessful){
                chats.clear()
                chats.addAll(it.result)
            }
        }
    }

    // TODO: animate scroll when new chat added
    LaunchedEffect(chats.size){
        lazyListState.animateScrollToItem(chats.size - 1)
    }

    DisposableEffect(Unit){
        val childEventListener = FireBaseRealtimeDatabase.childEventListener { chats.add(it) }
        messagesRef.addChildEventListener(childEventListener)
        onDispose {
            messagesRef.removeEventListener(childEventListener)
        }
    }


    val animatedIconBgColor by animateColorAsState(
        targetValue = if(message.isNotEmpty()) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
        animationSpec = tween(200)
    )

    Column(modifier = modifier.fillMaxSize()) {
        if(loading) Loader(modifier = Modifier.fillMaxWidth().weight(1f))

        AnimatedVisibility(
            visible = !loading,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 32.dp), contentPadding = PaddingValues(bottom = 64.dp)){
                items(chats.toList()){message ->
                    ChatBubble(message = message.text ?: "", sender = message.name ?: "", isOwner = true, date = DateUtils.getRelativeTimeSpanString(message.timeStamp ?: 0).toString())
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextInput(placeHolder = "Type message", value = message, modifier = Modifier.weight(1f), onValueChange = {it -> message = it})
            IconButton(onClick = { messagesRef
                .push()
                .setValue(
                    Message(
                        text = message,
                        name = "Lorem",
                        timeStamp = Date().time
                    )
                ) { error, _ ->
                    message = ""
                    Toast.makeText(context,if (error != null) "error" else "success" + " sending message",Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(animatedIconBgColor)
                .padding(8.dp)
                .align(Alignment.CenterVertically)
                ,
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
@Composable
private fun Loader(modifier: Modifier = Modifier){
    Column(modifier = modifier) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}
@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
private fun ChatRoomScreenPreview(){
    EcoEaseTheme() {
        ChatRoomScreen(navHostController = rememberNavController(), roomId = "ref")
    }
}
