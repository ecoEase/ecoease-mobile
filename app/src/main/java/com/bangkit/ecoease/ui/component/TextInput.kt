package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit = {}
){
    var value by rememberSaveable{
        mutableStateOf("")
    }
    val lightBlue = Color(0xff569DAA)
    OutlinedTextField(
        modifier = modifier,
        value = value,
        label = {
            Text("Label")
        },
        onValueChange = {
            value = it
            onChange(it)
        },
        shape = RoundedCornerShape(32.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = MaterialTheme.colors.background,
            textColor = MaterialTheme.colors.onBackground,
            focusedLabelColor = MaterialTheme.colors.onBackground,

            focusedBorderColor = Color.Magenta,
            unfocusedBorderColor = lightBlue
        )
    )
}

@Preview(showBackground = true)
@Composable
fun TextInputPreview(){
    EcoEaseTheme {
        TextInput()
    }
}