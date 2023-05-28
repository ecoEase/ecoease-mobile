package com.bangkit.ecoease.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.ui.component.RoundedButton
import com.bangkit.ecoease.ui.component.TextInput
import com.bangkit.ecoease.ui.theme.EcoEaseTheme


@Composable
fun AuthScreen(
    loginAction: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    var email by rememberSaveable{ mutableStateOf("") }
    var password by rememberSaveable{ mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 96.dp)
            .padding(horizontal = 32.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {//Logo
            Image(
                painter = painterResource(id = R.drawable.ecoease_icon),
                contentDescription = "ecoase icon",
                modifier = Modifier
                    .size(42.dp),
            )
            Text(text = stringResource(id = R.string.app_name), style = MaterialTheme.typography.h5)
        }
        Text("Login")
        TextInput(label = "Email", value = email, onValueChange = { it -> email = it})
        TextInput(label = "Password", value = password, onValueChange = { it -> password = it})
        RoundedButton(text = "login", modifier = Modifier.fillMaxWidth(), onClick = {
            loginAction()
            //if login success
            navHostController.navigate(Screen.Home.route){
                popUpTo(Screen.Auth.route) {
                    inclusive = true
                }
            }
        })
        Text("atau")
        Text("buat akun baru", modifier = Modifier.clickable { navHostController.navigate(Screen.Register.route) })
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview(){
    EcoEaseTheme {
        AuthScreen(navHostController = rememberNavController(), loginAction = {})
    }
}