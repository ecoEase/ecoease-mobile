package com.bangkit.ecoease.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.helper.InputValidation
import com.bangkit.ecoease.helper.generateUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: MainRepository) : ViewModel() {
    val emailValidation: InputValidation = InputValidation("",false, "")
    val passwordValidation: InputValidation = InputValidation("",false, "")
    private var _isLoginValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoginValid: StateFlow<Boolean> = _isLoginValid

    fun validateEmail(){
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        emailValidation.setErrorMessage(
            when {
                emailValidation.inputValue.value.isEmpty() -> "Email harus diisi!"
                !emailValidation.inputValue.value.matches(emailRegex.toRegex()) -> "Format email salah!"
                else -> ""
            }
        )
    }
    fun validatePassword(){
        passwordValidation.setErrorMessage(
            when {
                passwordValidation.inputValue.value.isEmpty() -> "Password harus diisi!"
                passwordValidation.inputValue.value.length < 8 -> "Password minimal harus 8 karakter!"
                else -> ""
            }
        )
    }
    fun login(onSuccess: () -> Unit){
        validateEmail()
        validatePassword()
        if(!emailValidation.isErrorState.value && !passwordValidation.isErrorState.value){
            // TODO: add auth api
            onSuccess()
            viewModelScope.launch(Dispatchers.IO) {
                repository.setUser()
                repository.setToken(generateUUID())
            }
        }

    }
    fun logout(){
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: change this code below to reset user when logout
            repository.resetUser()
            repository.setToken("")
        }
    }
}