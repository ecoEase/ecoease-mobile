package com.bangkit.ecoease.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.ui.component.Avatar
import com.bangkit.ecoease.ui.component.EditableText
import com.bangkit.ecoease.ui.theme.BluePrimary

@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
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
            isLarge = true
        )
        Text(text = "Maya", style = MaterialTheme.typography.h4)
        Text(text = "maya@gmail.com", style = MaterialTheme.typography.caption.copy(
            color = BluePrimary
        ))
        EditableText(label = "Nama", text = "Maya", onChange = {})
        EditableText(label = "No Telp.", text = "082132351498", onChange = {})
        EditableText(label = "Password", text = "082132351498", onChange = {})
    }
}