package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.model.request.Register
import com.bangkit.ecoease.data.remote.responseModel.RegisterData
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.helper.InputValidation
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RegisterViewModel(private val repository: MainRepository) : ViewModel() {
    val firstnameValidation: InputValidation = InputValidation("",false, "")
    val lastnameValidation: InputValidation = InputValidation("",false, "")
    val phoneNumberValidation: InputValidation = InputValidation("",false, "")
    val emailValidation: InputValidation = InputValidation("",false, "")
    val passwordValidation: InputValidation = InputValidation("",false, "")

    private val _uiStateProfileImage: MutableStateFlow<UiState<ImageCaptured>> = MutableStateFlow(
        UiState.Loading)
    val uiStateProfileImage: StateFlow<UiState<ImageCaptured>> = _uiStateProfileImage

    fun setProfileImage(imageCaptured: ImageCaptured){
        Log.d("TAG", "setImage: $imageCaptured")
        _uiStateProfileImage.value = UiState.Loading
        repository.setCapturedImage(imageCaptured)
    }
    fun getProfileImageUri(){
        viewModelScope.launch {
            try {
                repository.getCapturedImage()
                    .catch { error ->
                        Log.d("TAG", "getImageUri: ${error.message}")
                        _uiStateProfileImage.value = UiState.Error(error.message.toString())
                    }
                    .collect{imageCaptured ->
                        _uiStateProfileImage.value = UiState.Success(imageCaptured)
                        Log.d("TAG", "getImageCaptured success: ${imageCaptured.uri}")
                    }
            }catch (e: Exception){
                _uiStateProfileImage.value = UiState.Error(e.message.toString())
                Log.d("TAG", "getImageUri: ${e.message}")
            }
        }
    }
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
    fun validateFirstnameInput(){
        firstnameValidation.setErrorMessage(
            when {
                firstnameValidation.inputValue.value.isEmpty() -> "Nama  harus diisi!"
                else -> ""
            }
        )
    }
    fun validateLastnameInput(){
        lastnameValidation.setErrorMessage(
            when {
                firstnameValidation.inputValue.value.isEmpty() -> "Nama  harus diisi!"
                else -> ""
            }
        )
    }
    fun register(photoProfileFile: File, onSuccess: () -> Unit){
        run{
            validateFirstnameInput()
            validatePhoneNumberInput()
            validateEmailInput()
            validatePasswordInput()
        }
        val isAllInputValid = listOf(firstnameValidation, emailValidation, phoneNumberValidation, passwordValidation).all { !it.isErrorState.value }
        if(isAllInputValid){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // TODO: add register api
                    val fileRequestBody = photoProfileFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData(
                        "file",
                        "${emailValidation.inputValue.value}-profile.jpg",
                        fileRequestBody
                    )
                    val registerData = Register(
                        photoFile = filePart,
                        firstName = firstnameValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                        lastName = lastnameValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                        email = emailValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                        phoneNumber = phoneNumberValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                        password = passwordValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                    )
                    repository.registerUser(registerData).catch { error ->
                        Log.d("TAG", "register: $error")
                    }.collect{result ->
                        Log.d("TAG", "register: $result")
                        onSuccess()
                    }
                }catch (e: Exception){
                    throw e
                }
            }
        }
    }
}