package com.bangkit.ecoease.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.ui.component.Avatar
import com.bangkit.ecoease.ui.component.AvatarSize
import com.bangkit.ecoease.ui.component.DialogBox
import com.bangkit.ecoease.ui.component.TextReadOnly
import com.bangkit.ecoease.ui.theme.BluePrimary

@Composable
fun ProfileScreen(
    logoutAction: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    var openDialog by remember{ mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Avatar(
            imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
            size = AvatarSize.LARGE
        )
        Text(text = "Maya", style = MaterialTheme.typography.h4)
        Text(text = "maya@gmail.com", style = MaterialTheme.typography.caption.copy(
            color = BluePrimary
        ))
        TextReadOnly(label = "Nama", text = "Maya")
        TextReadOnly(label = "No Telp.", text = "082132351498")
        TextReadOnly(label = "Password", text = "082132351498")
        LogoutButton(action = {
            openDialog = true
        })
        DialogBox(text = stringResource(R.string.logout_confirm), onDissmiss = { openDialog = false }, isOpen = openDialog, onAccept = {
            logoutAction()
            navHostController.navigate(Screen.Auth.route)
        })
    }
}

@Composable
fun LogoutButton(
    action: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { action() }
    ,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(imageVector = Icons.Default.Logout, contentDescription = "logout")
        Text(text = "Logout")
    }
}