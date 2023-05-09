package com.bangkit.ecoease.data.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraViewModel: ViewModel() {
    private val _uiStateImageUri: MutableStateFlow<UiState<Uri>> = MutableStateFlow(UiState.Loading)
    val uiStateImageUri: StateFlow<UiState<Uri>> = _uiStateImageUri

    fun setImageUri(uri: Uri){
        _uiStateImageUri.value = UiState.Success(uri)
    }
}