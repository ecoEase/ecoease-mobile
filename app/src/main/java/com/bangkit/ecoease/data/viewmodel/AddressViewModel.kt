package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.room.model.Address
import com.bangkit.ecoease.helper.InputValidation
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressViewModel(private val repository: MainRepository): ViewModel() {
    val emptyAddress = Address(
        id = "",
        name = "",
        detail = "",
        district = "",
        city = "",
        selected = false
    )
    private var _savedAddress: MutableStateFlow<UiState<List<Address>>> = MutableStateFlow(UiState.Loading)
    private var _tempSelectedAddress: MutableStateFlow<Address> = MutableStateFlow(emptyAddress)
    private var _selectedAddress: MutableStateFlow<UiState<Address>> = MutableStateFlow(UiState.Loading)
    private var _message: MutableStateFlow<String> = MutableStateFlow("")
    //input validation
    val addressNameValidation = InputValidation("",  false, "")
    val cityValidation = InputValidation("",  false, "")
    val districtValidation = InputValidation("", false, "")
    val detailValidation = InputValidation("", false, "")

    val savedAddress: StateFlow<UiState<List<Address>>> = _savedAddress
    val tempSelectedAddress: StateFlow<Address> = _tempSelectedAddress
    val selectedAddress: StateFlow<UiState<Address>> = _selectedAddress
    val message: StateFlow<String> = _message

    fun validateNameInput(){
        addressNameValidation.setErrorMessage(
            when {
                addressNameValidation.inputValue.value.isEmpty() -> "Nama alamat harus diisi!"
                else -> ""
            }
        )
    }
    fun validateCityInput(){
        cityValidation.setErrorMessage(
            when {
                cityValidation.inputValue.value.isEmpty() -> "Kota alamat harus diisi!"
                else -> ""
            }
        )
    }
    fun validateDistrictInput(){
        districtValidation.setErrorMessage(
            when {
                districtValidation.inputValue.value.isEmpty() -> "Kecamatan alamat harus diisi!"
                else -> ""
            }
        )
    }
    fun validateDetailInput(){
        detailValidation.setErrorMessage(
            when {
                detailValidation.inputValue.value.isEmpty() -> "Detail alamat harus diisi!"
                else -> ""
            }
        )
    }
    fun loadSavedAddress(){
        viewModelScope.launch(Dispatchers.IO) {
            _selectedAddress.value = UiState.Loading
            repository.getSavedAddress().catch {
                _savedAddress.value = UiState.Error("error: ${it.message}")
            }.collect{
                _savedAddress.value = UiState.Success(it)
            }
        }
    }
    fun loadSelectedAddress(){
        viewModelScope.launch(Dispatchers.IO) {
            _selectedAddress.value = UiState.Loading
            delay(200)
            repository.getSelectedAddress().catch{
                _selectedAddress.value = UiState.Error("error: ${it.message}")
            }.collect{
                _selectedAddress.value = UiState.Success(it ?: emptyAddress)
            }
        }
    }
    fun reloadSelectedAddress(){
        _selectedAddress.value = UiState.Loading
    }
    fun pickSelectedAddress(address: Address){
        _tempSelectedAddress.value = address
    }
    fun confirmSelectedAddress(address: Address, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSelectedAddress(address)
            withContext(Dispatchers.Main){
                onSuccess()
            }
        }
    }
    fun addNewAddress(address: Address){
        run {
            validateNameInput()
            validateCityInput()
            validateDetailInput()
            validateDistrictInput()
        }//validate first
        val isAllInputValid = listOf(addressNameValidation, cityValidation, districtValidation, detailValidation).all { !it.isErrorState.value }
        if(isAllInputValid){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val user = repository.getUser()
                    val newAddress = address.copy(id = user.first().id)
                    repository.addAddress(newAddress)
                    _savedAddress.value = UiState.Loading//trigger loading so in ui it will call the loadSavedAddress method
                }catch (e: Exception){
                    Log.d("TAG", "addNewAddress: ${e.message}")
                }
            }
        }else{
            throw Exception("Fields must be valid")
        }
    }
    fun deleteAddress(address: Address){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteAddress(address)
                if(_selectedAddress.value is UiState.Success && address == (_selectedAddress.value as UiState.Success<Address>).data){
                    _selectedAddress.value = UiState.Loading
                }
                _savedAddress.value = UiState.Loading//trigger loading so in ui it will call the loadSavedAddress method
            }catch (e: Exception){
                Log.d("TAG", "deleteAddress: ${e.message}")
            }
        }
    }
    fun reloadSavedAddress() {
        _savedAddress.value = UiState.Loading
    }
}