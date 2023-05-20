package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.model.Address
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddressViewModel(private val repository: MainRepository): ViewModel() {
    private var _savedAddress: MutableStateFlow<UiState<List<com.bangkit.ecoease.data.room.dao.Address>>> = MutableStateFlow(UiState.Loading)
    val savedAddress: MutableStateFlow<UiState<List<com.bangkit.ecoease.data.room.dao.Address>>> = _savedAddress

    fun loadSavedAddress(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSavedAddress().catch {
                _savedAddress.value = UiState.Error("error: ${it.message}")
            }.collect{
                Log.d("TAG", "loadSavedAddress: $it")
                _savedAddress.value = UiState.Success(it)
            }
        }
    }

    fun addNewAddress(newAddress: com.bangkit.ecoease.data.room.dao.Address){
        Log.d("TAG", "addNewAddress new : $newAddress")
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAddress(newAddress)
        }
    }
}