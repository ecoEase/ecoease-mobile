package com.bangkit.ecoease.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.ui.theme.BluePrimary
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.bangkit.ecoease.R
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.ui.theme.LightTosca

@Composable
fun DropDown(
    listItem: List<String>,
    label: String,
    onChange: (String) -> Unit = {},
    onSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
){
    var expanded by remember{
        mutableStateOf(false)
    }
    var selectedText by rememberSaveable{
        mutableStateOf("")
    }
    var isError by rememberSaveable {
        mutableStateOf(false)
    }
    var listItemState: List<String> by remember {
        mutableStateOf(listItem)
    }

    val animatedBackgrounColor by animateColorAsState(
        targetValue = if(selectedText.isNotEmpty()) LightTosca.copy(alpha = 0.3f) else MaterialTheme.colors.background,
        animationSpec = tween(200)
    )

    Column {
        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged { focusState -> expanded = focusState.hasFocus }
                .zIndex(2f)
            ,
            maxLines = 1,
            value = selectedText,
            label = {
                Text(label)
            },
            readOnly = true,
            isError = isError,
            trailingIcon = {
               Icon(
                   if(expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                   contentDescription = "arrow dropdown",
                   modifier = Modifier
                       .clickable { expanded = !expanded }
               )
            },
            onValueChange = {
//                onChange(it)
                selectedText = it
                expanded = true
                listItemState = if(it.isNotEmpty()) listItemState.filter { item -> item.contains(it) } else listItem
            },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = animatedBackgrounColor,
                textColor = MaterialTheme.colors.onBackground,
                focusedLabelColor = MaterialTheme.colors.onBackground,
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = BluePrimary
            )
        )
        Box(modifier = Modifier.height(4.dp))
        AnimatedVisibility(visible = expanded) {
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 152.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colors.background)
                    .border(BorderStroke(1.dp, LightTosca), shape = RoundedCornerShape(32.dp))
                ,
                contentPadding = PaddingValues(vertical = 8.dp)
            ){
                items(listItemState){
                    isError = false
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected(it)
                                selectedText = it
                                expanded = false
                            }
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                        ,
                        text = it
                    )
                }
                if(listItemState.isEmpty()){
                    isError = true
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                            ,
                            text = stringResource(R.string.not_found),
                            color = DarkGrey
                        )
                    }
                }
        }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDropDown(){
    EcoEaseTheme {
        Column {
            DropDown(listItem = listOf("lorem", "ipsum","lorem", "ipsum","lorem", "ipsum","lorem", "ipsum",), label = "test")
            TextInput(label = "test dropdown")

        }
    }
}