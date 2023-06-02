package com.bangkit.ecoease.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.helper.InputValidation
import com.bangkit.ecoease.ui.component.RoundedButton
import com.bangkit.ecoease.ui.component.TextInput
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

@Composable
fun RegisterScreen(
    nameValidation: InputValidation,
    emailValidation: InputValidation,
    phoneNumberValidation: InputValidation,
    passwordValidation: InputValidation,
    validateNameInput: () -> Unit,
    validateEmailInput: () -> Unit,
    validatePhoneNumberInput: () -> Unit,
    validatePasswordInput: () -> Unit,
    navHostController: NavHostController,
    onRegister: (onSuccess: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 96.dp)
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState())
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
        Text("Registrasi")
        TextInput(
            label = "Nama",
            onValueChange = { nameValidation.updateInputValue(it) },
            value = nameValidation.inputValue.collectAsState().value,
            isError = nameValidation.isErrorState.collectAsState().value,
            errorMessage = nameValidation.getErrorMessage(),
            validate = validateNameInput,
            imeAction = ImeAction.Next
        )
        TextInput(
            label = "Email",
            onValueChange = { emailValidation.updateInputValue(it) },
            value = emailValidation.inputValue.collectAsState().value,
            isError = emailValidation.isErrorState.collectAsState().value,
            errorMessage = emailValidation.getErrorMessage(),
            keyboardType = KeyboardType.Email,
            validate = validateEmailInput,
            imeAction = ImeAction.Next
        )
        TextInput(
            label = "Nomor Telepon",
            onValueChange = { phoneNumberValidation.updateInputValue(it) },
            value = phoneNumberValidation.inputValue.collectAsState().value,
            isError = phoneNumberValidation.isErrorState.collectAsState().value,
            errorMessage = phoneNumberValidation.getErrorMessage(),
            keyboardType = KeyboardType.Number,
            validate = validatePhoneNumberInput,
            imeAction = ImeAction.Next
        )
        TextInput(
            label = "Password",
            onValueChange = { passwordValidation.updateInputValue(it) },
            value = passwordValidation.inputValue.collectAsState().value,
            isError = passwordValidation.isErrorState.collectAsState().value,
            errorMessage = passwordValidation.getErrorMessage(),
            isPassword = true,
            validate = validatePasswordInput,
            imeAction = ImeAction.Next
        )
        RoundedButton(text = "registrasi", modifier = Modifier.fillMaxWidth(), onClick = {
            onRegister{
                navHostController.navigate(Screen.Auth.route)
            }
        })
        Text("atau")
        Text("login akun", modifier = Modifier.clickable { navHostController.navigate(Screen.Auth.route) })
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RegisterScreenPreview(){
//    EcoEaseTheme {
//        RegisterScreen(navHostController = rememberNavController())
//    }
//}