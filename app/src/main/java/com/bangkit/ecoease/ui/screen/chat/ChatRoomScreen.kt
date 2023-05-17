package com.bangkit.ecoease.ui.screen.chat

import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.bangkit.ecoease.BuildConfig
import com.bangkit.ecoease.data.model.Message
import com.bangkit.ecoease.ui.component.ChatBubble
import com.bangkit.ecoease.ui.component.TextInput
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.google.firebase.database.FirebaseDatabase
import java.util.*

@Composable
fun ChatRoomScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    val db: FirebaseDatabase = FirebaseDatabase.getInstance (BuildConfig.firebase_realtime_db_url)
    val messagesRef = db.reference.child("ref")
    val context = LocalContext.current
//    val messagesRef = FireBaseRealtimeDatabase.createMessageRef("message")
    var message: String by rememberSaveable {
        mutableStateOf("")
    }
    // TODO: move chat bussiness logic in a viewmodel
    messagesRef.get().addOnCompleteListener{task ->
        if(task.isSuccessful){
            val result = task.result
            result?.let {
                val final = result.children.map { snapshot ->
                    snapshot.getValue(Message::class.java)!!
                }
                Log.d("TAG", "ChatRoomScreen: $final")
            }
        }
    }

    val animatedIconBgColor by animateColorAsState(
        targetValue = if(message.isNotEmpty()) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
        animationSpec = tween(200)
    )

    val fakeChats = listOf<Message>(
        Message("lorem", "ipsum", 10000),
        Message("lorem", "ipsum", 10001),
        Message("lorem", "ipsum", 10002),
        Message("lorem", "ipsum", 10003),
        Message("lorem", "ipsum", 10004),
    )

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)){
            items(fakeChats, key = { "${it.name}-${it.timeStamp}" }){message ->
                ChatBubble(message = message.text ?: "", sender = message.name ?: "", isOwner = true, date = DateUtils.getRelativeTimeSpanString(message.timeStamp ?: 0).toString())
            }
            item {
                ChatBubble(message = fakeChats[0].text ?: "", sender = fakeChats[0].name ?: "", isOwner = false, date = DateUtils.getRelativeTimeSpanString(fakeChats[0].timeStamp ?: 0).toString())
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            TextInput(placeHolder = "Type message", onChange = {message = it}, modifier = Modifier.weight(1f))
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

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun ChatRoomScreenPreview(){
    EcoEaseTheme() {
        ChatRoomScreen(navHostController = rememberNavController())
    }
}
