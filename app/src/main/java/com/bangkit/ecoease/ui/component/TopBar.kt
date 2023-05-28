package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.data.Screen

@Composable
fun TopBar(
    currentRoute: String?,
    navController: NavHostController,
    isUseNavButton: Boolean = false,
    isUseAvatar: Boolean = false,
    onTapNavButton: () -> Unit = {},
    onTapAvatar: () -> Unit = {},
    avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=464&q=80",
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        title = {
            when {
                currentRoute?.substringBefore("?") == Screen.ChatRoom.route -> ChatRoomTitle(
                    avatarUrl = avatarUrl,
                    title = Screen.ChatRoom.getTitle()
                        ?.let { text -> text.replaceFirstChar { it.uppercase() } } ?: "")
                currentRoute != Screen.Home.route -> Text(
                    text = currentRoute?.let { text -> if (text == Screen.DetailOrder.route) "Detail order" else text.replaceFirstChar { it.uppercase() } }
                        ?.replace("_", " ") ?: "",
                    textAlign = TextAlign.Center,
                )
            }
        },
        actions = {
            if (isUseAvatar) Avatar(
                imageUrl = avatarUrl,
                size = AvatarSize.EXTRA_SMALL,
                modifier = Modifier
                    .padding(end = 32.dp)
                    .clickable { onTapAvatar() }
            )
        },
        navigationIcon = {
            if (isUseNavButton) IconButton(onClick = onTapNavButton) {
                Icon(
                    Icons.Filled.ArrowBack,
                    "backIcon"
                )
            }
        },
    )
}

@Composable
fun ChatRoomTitle(
    avatarUrl: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Avatar(imageUrl = avatarUrl, size = AvatarSize.EXTRA_SMALL,)
        Text(text = title, textAlign = TextAlign.Center)
    }
}
