package com.bangkit.ecoease.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector){
    object Onboard : Screen(route = "home", icon = Icons.Default.Start)
    object Home : Screen(route = "home", icon = Icons.Default.Home)
    object Temp : Screen(route = "temp/{path}", icon = Icons.Default.Home){
        fun setImage(path: String) = "temp/$path"
    }
    object Camera : Screen(route = "camera", icon = Icons.Default.CameraAlt)
    object History : Screen(route = "history", icon = Icons.Default.History)
    object Profile : Screen(route = "profile", icon = Icons.Default.AccountCircle)
    object Map : Screen(route = "map", icon = Icons.Default.Map)
}