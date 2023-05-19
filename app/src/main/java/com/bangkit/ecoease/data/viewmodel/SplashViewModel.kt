package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.helper.generateUUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: MainRepository): ViewModel() {
    private val _isLoading = mutableStateOf(true)
    private val _isReadOnboard = MutableStateFlow(false)
    private val _isLogged = MutableStateFlow(false)
    val isLoading: State<Boolean> = _isLoading
    val isReadOnboard: StateFlow<Boolean> = _isReadOnboard
    val isLogged: StateFlow<Boolean> = _isLogged

    init {
        viewModelScope.launch {
            _isReadOnboard.value = repository.getIsFinishOnboard()
            _isLogged.value = repository.getToken().isNotEmpty()
            //adding delay so when splash screen is finish the determine screen directly show
            delay(4000)
            _isLoading.value = false
        }
    }
    fun finishedOnBoard(){
        viewModelScope.launch {
            repository.finishOnBoard()
        }
    }
    fun login(){
        viewModelScope.launch {
            repository.setToken(generateUUID())
        }
    }
    fun logout(){
        viewModelScope.launch {
            repository.setToken("")
        }
    }
}