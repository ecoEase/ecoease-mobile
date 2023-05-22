package com.bangkit.ecoease.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.room.model.Address
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddressViewModel(private val repository: MainRepository): ViewModel() {
    private var _savedAddress: MutableStateFlow<UiState<List<Address>>> = MutableStateFlow(UiState.Loading)
    private var _tempSelectedAddress: MutableStateFlow<Address?> = MutableStateFlow(null)
    private var _selectedAddress: MutableStateFlow<UiState<Address?>> = MutableStateFlow(UiState.Loading)
    private var _message: MutableStateFlow<String> = MutableStateFlow("")

    val savedAddress: StateFlow<UiState<List<Address>>> = _savedAddress
    val tempSelectedAddress: StateFlow<Address?> = _tempSelectedAddress
    val selectedAddress: StateFlow<UiState<Address?>> = _selectedAddress
    val message: StateFlow<String> = _message
    fun loadSavedAddress(){
        viewModelScope.launch(Dispatchers.IO) {
            delay(200)
            repository.getSavedAddress().catch {
                _savedAddress.value = UiState.Error("error: ${it.message}")
            }.collect{
                _savedAddress.value = UiState.Success(it)
            }
        }
    }

    fun loadSelectedAddress(){
        viewModelScope.launch(Dispatchers.IO) {
            delay(200)
            repository.getSelectedAddress().catch{
                _selectedAddress.value = UiState.Error("error: ${it.message}")
            }.collect{
                _selectedAddress.value = UiState.Success(it ?: Address(
                    id = "",
                    name = "",
                    detail = "",
                    district = "",
                    city = "",
                    selected = false
                ))
            }
        }
    }

    fun reloadSelectedAddress(){
        _selectedAddress.value = UiState.Loading
    }

    fun pickSelectedAddress(address: Address){
        _tempSelectedAddress.value = address
    }

    fun confirmSelectedAddress(address: Address){
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSelectedAddress(address)
        }
    }

    fun addNewAddress(newAddress: com.bangkit.ecoease.data.room.model.Address){
        viewModelScope.launch {
            repository.addAddress(newAddress)
            _savedAddress.value = UiState.Loading//trigger loading so in ui it will call the loadSavedAddress method
        }
    }

    fun deleteAddress(address: com.bangkit.ecoease.data.room.model.Address){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAddress(address)
            _savedAddress.value = UiState.Loading//trigger loading so in ui it will call the loadSavedAddress method
        }
    }

    fun reloadSavedAddress() {
        _savedAddress.value = UiState.Loading
    }
}