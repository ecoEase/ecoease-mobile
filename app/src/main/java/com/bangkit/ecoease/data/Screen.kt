package com.bangkit.ecoease.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector){
    object Onboard : Screen(route = "onboard", icon = Icons.Default.Start)
    object Home : Screen(route = "home", icon = Icons.Default.Home)
    object Temp : Screen(route = "temp/{path}", icon = Icons.Default.Home){
        fun setImage(path: String) = "temp/$path"
    }
    object Camera : Screen(route = "camera", icon = Icons.Default.CameraAlt)
    object History : Screen(route = "history", icon = Icons.Default.History)
    object Profile : Screen(route = "profile", icon = Icons.Default.AccountCircle)
    object Map : Screen(route = "map", icon = Icons.Default.Map)
    object Auth : Screen(route = "auth", icon = Icons.Default.Login)
    object Register : Screen(route = "register", icon = Icons.Default.AppRegistration)
    object Order : Screen(route = "order", icon = Icons.Default.Reorder)
    object DetailOrder : Screen(route = "detail order", icon = Icons.Default.Reorder)
    object ChangeAddress : Screen(route = "change address", icon = Icons.Default.Reorder)
    object OrderSuccess : Screen(route = "order success", icon = Icons.Default.Reorder)
    object UsersChats : Screen(route = "users chats", icon = Icons.Default.Chat)
    object ChatRoom : Screen(route = "chat room", icon = Icons.Default.Chat){

    }
}