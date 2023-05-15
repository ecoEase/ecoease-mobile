package com.bangkit.ecoease.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheet(
    onActionButtonClicked: () -> Unit = {},
    label: String,
    actionName: String,
    information: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
){
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if(isActive) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
        animationSpec = tween(durationMillis = 200)
    )

    Column(modifier = modifier
        .fillMaxWidth()
        .height(98.dp)
        .background(animatedBackgroundColor)
        .padding(horizontal = 32.dp, vertical = 16.dp)
    ,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = MaterialTheme.typography.h5)
            Text(text = information, style = MaterialTheme.typography.h5)
        }
        RoundedButton(text = actionName, onClick = onActionButtonClicked)
    }
}