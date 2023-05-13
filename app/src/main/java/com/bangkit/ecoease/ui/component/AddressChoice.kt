package com.bangkit.ecoease.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.bangkit.ecoease.ui.theme.GreenSecondary

@Composable
fun AddressChoice(
    addressName: String,
    addressDetail1: String,
    addressDetail2: String,
    modifier: Modifier = Modifier
){
    var clicked by rememberSaveable{
        mutableStateOf(false)
    }
    val animateColorRadio by animateColorAsState(
        targetValue = if(clicked) MaterialTheme.colors.primary else Color.Transparent,
        animationSpec = tween(200)
    )
    val animateColorBorder by animateColorAsState(
        targetValue = if(clicked) MaterialTheme.colors.primary else GreenSecondary,
        animationSpec = tween(200)
    )
    Card(
        border = BorderStroke(width = 1.dp, color = animateColorBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
        ,
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 16.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(animateColorRadio)
                    .border(border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primary), shape = CircleShape)
                    .clickable { clicked = !clicked }
            )
            Box(modifier = Modifier.width(27.dp))
            Column {
                Text(text = addressName)
                Box(modifier = Modifier.height(8.dp))
                Text(text = addressDetail1, style = MaterialTheme.typography.body2)
                Box(modifier = Modifier.height(4.dp))
                Text(text = addressDetail2, style = MaterialTheme.typography.body2.copy(
                    color = DarkGrey
                ))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddressChoicePreview(){
    EcoEaseTheme() {
        AddressChoice(addressName = "Alamat 1", addressDetail1 = "Jalan yang lurus lorem ipsum blabal", addressDetail2 = "Malang, Jawa Timur")
    }
}