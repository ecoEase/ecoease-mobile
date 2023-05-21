package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddressViewModel(private val repository: MainRepository): ViewModel() {
    private var _savedAddress: MutableStateFlow<UiState<List<com.bangkit.ecoease.data.room.model.Address>>> = MutableStateFlow(UiState.Loading)
    private var _message: MutableStateFlow<String> = MutableStateFlow("")

    val savedAddress: StateFlow<UiState<List<com.bangkit.ecoease.data.room.model.Address>>> = _savedAddress
    val message: StateFlow<String> = _message
    fun loadSavedAddress(){
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            repository.getSavedAddress().catch {
                _savedAddress.value = UiState.Error("error: ${it.message}")
            }.collect{
                Log.d("TAG", "loadSavedAddress: $it")
                _savedAddress.value = UiState.Success(it)
            }
        }
    }

    fun addNewAddress(newAddress: com.bangkit.ecoease.data.room.model.Address){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAddress(newAddress)
            _message.value = "Success adding new address!"
            _savedAddress.value = UiState.Loading//trigger loading so in ui it will call the loadSavedAddress method
        }
    }

    fun deleteAddress(address: com.bangkit.ecoease.data.room.model.Address){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAddress(address)
            _message.value = "Success delete ${address.name} address!"
            _savedAddress.value = UiState.Loading//trigger loading so in ui it will call the loadSavedAddress method
        }
    }

    fun reloadSavedAddress() {
        _savedAddress.value = UiState.Loading
    }
}