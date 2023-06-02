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

class RegisterViewModel(private val repository: MainRepository) : ViewModel() {
    val nameValidation: InputValidation = InputValidation("",false, "")
    val phoneNumberValidation: InputValidation = InputValidation("",false, "")
    val emailValidation: InputValidation = InputValidation("",false, "")
    val passwordValidation: InputValidation = InputValidation("",false, "")

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
    fun validatePhoneNumberInput(){
        phoneNumberValidation.setErrorMessage(
            when {
                phoneNumberValidation.inputValue.value.isEmpty() -> "Nomor telepon harus diisi!"
                phoneNumberValidation.inputValue.value.length < 11 -> "Nomor telepon minimal harus 11 karakter!"
                phoneNumberValidation.inputValue.value.length > 13 -> "Nomor telepon maximal harus 13 karakter!"
                else -> ""
            }
        )
    }
    fun validateNameInput(){
        nameValidation.setErrorMessage(
            when {
                nameValidation.inputValue.value.isEmpty() -> "Nama  harus diisi!"
                else -> ""
            }
        )
    }
    fun register(onSuccess: () -> Unit){
        run{
            validateNameInput()
            validatePhoneNumberInput()
            validateEmailInput()
            validatePasswordInput()
        }
        val isAllInputValid = listOf(nameValidation, emailValidation, phoneNumberValidation, passwordValidation).all { !it.isErrorState.value }
        if(isAllInputValid){
            // TODO: add register api
            onSuccess()
        }

    }
}