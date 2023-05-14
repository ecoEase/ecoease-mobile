package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.ui.theme.BluePrimary
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

@Composable
fun TextInput(
    label: String,
    isTextArea: Boolean = false,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit = {}
){
    var value by rememberSaveable{
        mutableStateOf("")
    }
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(if(isTextArea) 112.dp else 56.dp)
        ,
        value = value,
        label = {
            Text(label)
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
            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = BluePrimary
        )
    )
}

@Preview(showBackground = true)
@Composable
fun TextInputPreview(){
    EcoEaseTheme {
        TextInput(label = "Label")
    }
}