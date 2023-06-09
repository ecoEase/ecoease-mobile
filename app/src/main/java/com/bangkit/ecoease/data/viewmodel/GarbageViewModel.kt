package com.bangkit.ecoease.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.room.model.Garbage
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GarbageViewModel(private val repository: MainRepository): ViewModel() {
    private var _garbageState = MutableStateFlow<UiState<List<Garbage>>>(UiState.Loading)
    val garbageState: StateFlow<UiState<List<Garbage>>> = _garbageState
    fun getAllGarbage(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAllGarbage().catch {
                    _garbageState.value = UiState.Error("error ${it.message}")
                }.collect{garbage ->
                    _garbageState.value = UiState.Success(garbage)
                }
            }catch (e: Exception){
                _garbageState.value = UiState.Error("error ${e.message}")
            }
        }
    }
    fun reloadGarbage(){
        _garbageState.value = UiState.Loading
    }
}