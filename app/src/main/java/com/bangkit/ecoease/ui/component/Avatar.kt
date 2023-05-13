package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bangkit.ecoease.R
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

@Composable
fun Avatar(
    isLarge: Boolean = false,
    imageUrl: String,
    modifier: Modifier = Modifier
){
    AsyncImage(
        model = imageUrl,
        contentDescription = "user avatar",
        modifier = modifier
            .size(if (isLarge) 128.dp else 64.dp)
            .clip(CircleShape)
            .background(DarkGrey)
        ,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
        error = painterResource(id = R.drawable.ic_launcher_foreground)
    )
}

@Preview(showBackground = true)
@Composable
fun AvatarPreview(){
    EcoEaseTheme() {
        Avatar(imageUrl = "https://images.unsplash.com/photo-1683538128801-827200d50e0c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80")
    }
}