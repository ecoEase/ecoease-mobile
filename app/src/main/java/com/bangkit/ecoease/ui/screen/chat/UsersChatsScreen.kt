package com.bangkit.ecoease.ui.screen.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.ui.component.RoundedButton

@Composable
fun UsersChatsScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.fillMaxSize()) {
        RoundedButton(text = "chat", onClick = {navHostController.navigate(Screen.ChatRoom.route)})
    }
}