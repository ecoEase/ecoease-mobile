package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.ui.theme.BluePrimary
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditableText(
    text: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    var value: String by rememberSaveable{
        mutableStateOf(text)
    }
    var onEdit: Boolean by rememberSaveable{
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier
            .focusRequester(focusRequester)
        ,
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

            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = BluePrimary
        ),
        trailingIcon = {
            if(!onEdit){
                Icon(Icons.Default.Edit, contentDescription = "edit icon", modifier = Modifier
                    .clickable {
                        onEdit = true
                        focusRequester.requestFocus()
                    }
                )
            }
            if(onEdit && value.isNotEmpty()){
                Icon(Icons.Default.Close, contentDescription = "edit icon", modifier = Modifier
                    .clickable { value = "" }
                )
            }
        },
        isError = value.isEmpty(),
    )
}

@Preview(showBackground = true)
@Composable
fun EditableTextPreview(){
    EcoEaseTheme() {
        EditableText(onChange = {}, text = "Lorem ipsum")
    }
}