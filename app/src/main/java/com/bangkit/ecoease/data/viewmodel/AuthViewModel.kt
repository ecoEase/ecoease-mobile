package com.bangkit.ecoease.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.helper.generateUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: MainRepository) : ViewModel() {
    fun login(){
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: change this code below to set user when there is success login
            repository.setUser()
            repository.setToken(generateUUID())
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