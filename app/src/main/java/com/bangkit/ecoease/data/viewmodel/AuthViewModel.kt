package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.model.request.Login
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.helper.InputValidation
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val repository: MainRepository) : ViewModel() {
    val emailValidation: InputValidation = InputValidation("",false, "")
    val passwordValidation: InputValidation = InputValidation("",false, "")
    private var _isLoginValid: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Success(false))
    val isLoginValid: StateFlow<UiState<Boolean>> = _isLoginValid

    fun validateEmailInput(){
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        emailValidation.setErrorMessage(
            when {
                emailValidation.inputValue.value.isEmpty() -> "Email harus diisi!"
                !emailValidation.inputValue.value.matches(emailRegex.toRegex()) -> "Format email salah!"
                else -> ""
            }
        )
    }
    fun validatePasswordInput(){
        passwordValidation.setErrorMessage(
            when {
                passwordValidation.inputValue.value.isEmpty() -> "Password harus diisi!"
                passwordValidation.inputValue.value.length < 8 -> "Password minimal harus 8 karakter!"
                else -> ""
            }
        )
    }
    fun login(onSuccess: () -> Unit){
        run{
            validateEmailInput()
            validatePasswordInput()
        }
        val isAllInputValid = listOf(emailValidation, passwordValidation).all { !it.isErrorState.value }
        if(isAllInputValid){
            _isLoginValid.value = UiState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    repository.loginUser(
                        Login(email = emailValidation.inputValue.value,password = passwordValidation.inputValue.value)
                    ).catch { error ->
                        _isLoginValid.value = UiState.Error("error: ${error.message}")
                    }.collect{
                        _isLoginValid.value = UiState.Success(true)
                        withContext(Dispatchers.Main){
                            onSuccess()
                        }
                    }
                }catch (e: Exception){
                    Log.d("TAG", "login error: $e")
                    _isLoginValid.value = UiState.Error("error: ${e.message}")
                }
            }
        }

    }
    fun logout(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetUser()
            repository.setToken("")
        }
    }
}