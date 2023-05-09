package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

@Composable
fun Counter(
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit = {}
){
    var counter by remember{
        mutableStateOf(0)
    }
    
    LaunchedEffect(counter){
        onValueChange(counter)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CounterButton(
            icon = Icons.Default.Remove,
            description = "minus",
            onClick = { if(counter > 0) counter -= 1 }
        )
        Text(text = counter.toString())
        CounterButton(
            icon = Icons.Default.Add,
            description = "plus",
            onClick = { counter += 1 }
        )
    }
}

@Composable
fun CounterButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit = {}
){
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.primary)
            .clickable { onClick }
    ){
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CounterPreview(){
    EcoEaseTheme {
        Counter()
    }
}
@Preview(showBackground = true)
@Composable
fun CounterButtonPreview(){
    EcoEaseTheme {
        CounterButton(
            icon = Icons.Default.Add,
            description = ""
        )
    }
}