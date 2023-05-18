package com.bangkit.ecoease.ui.component

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.data.Screen

@Composable
fun TopBar(
    isShown: Boolean,
    showIconNav: Boolean,
    title: String,
    navigationIconAction: () -> Unit = {},
    modifier: Modifier = Modifier
){
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
            title = { Text(text = title,textAlign = TextAlign.Center) },
            navigationIcon = { if (showIconNav) IconButton(onClick = { navigationIconAction() }) {
                Icon(Icons.Filled.ArrowBack, "backIcon")}
            },
        )
}