package com.bangkit.ecoease.data

sealed class Screen(val route: String){
    object Home : Screen("home")
    object Temp : Screen("temp/{path}"){
        fun setImage(path: String) = "temp/$path"
    }
    object Camera : Screen("camera")
}