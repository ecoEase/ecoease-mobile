package com.bangkit.ecoease.data.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CameraViewModel(private val repository: MainRepository): ViewModel() {
    private val _uiStateImageUri: MutableStateFlow<UiState<Uri>> = MutableStateFlow(UiState.Loading)
    val uiStateImageUri: StateFlow<UiState<Uri>> = _uiStateImageUri

    fun setImageUri(uri: Uri){
        _uiStateImageUri.value = UiState.Loading
        repository.setCapturedImageUri(uri)
    }

    fun getImageUri(){
        viewModelScope.launch {
            try {
                repository.getCapturedImageUri()
                    .catch { error ->
                        Log.d("TAG", "getImageUri: ${error.message}")
                        _uiStateImageUri.value = UiState.Error(error.message.toString())
                    }
                    .collect{uri ->
                        _uiStateImageUri.value = UiState.Success(uri)
                        Log.d("TAG", "getImageUri success: ${uri}")
                    }
            }catch (e: Exception){
                _uiStateImageUri.value = UiState.Error(e.message.toString())
                Log.d("TAG", "getImageUri: ${e.message}")
            }
        }
    }
}