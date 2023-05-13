package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.ui.theme.BlueSecondary
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

enum class RoundedButtonType{
    PRIMARY,
    SECONDARY
}
@Composable
fun RoundedButton(
    text: String,
    type: RoundedButtonType = RoundedButtonType.PRIMARY,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .widthIn(min = 100.dp)
        ,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (type == RoundedButtonType.PRIMARY) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
            contentColor = if (type == RoundedButtonType.PRIMARY) Color.White else BlueSecondary
        )
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun RoundedButtonPreview(){
    EcoEaseTheme {
        RoundedButton("Primary")
    }
}


@Preview(showBackground = true)
@Composable
fun RoundedButtonSecPreview(){
    EcoEaseTheme {
        RoundedButton("Secondary", RoundedButtonType.SECONDARY)
    }
}