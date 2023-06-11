package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.event.MyEvent
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class CameraViewModel(private val repository: MainRepository) : ViewModel() {
    private val _uiStateImageCaptured: MutableStateFlow<UiState<ImageCaptured>> = MutableStateFlow(UiState.Loading)
    val uiStateImageCaptured: StateFlow<UiState<ImageCaptured>> = _uiStateImageCaptured
    private var _predictResultUiState: MutableStateFlow<UiState<String>> = MutableStateFlow(UiState.Loading)
    val predictResultUiState: StateFlow<UiState<String>> = _predictResultUiState
    private val eventChannel = Channel<MyEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun setImage(imageCaptured: ImageCaptured) {
        _uiStateImageCaptured.value = UiState.Loading
        repository.setCapturedImage(imageCaptured)
    }

    fun getImageUri() {
        viewModelScope.launch {
            try {
                repository.getCapturedImage()
                    .catch { error ->
                        Log.d("TAG", "getImageUri: ${error.message}")
                        _uiStateImageCaptured.value = UiState.Error(error.message.toString())
                    }
                    .collect { imageCaptured ->
                        _uiStateImageCaptured.value = UiState.Success(imageCaptured)
                        Log.d("TAG", "getImageCaptured success: ${imageCaptured.uri}")
                    }
            } catch (e: Exception) {
                _uiStateImageCaptured.value = UiState.Error(e.message.toString())
                Log.d("TAG", "getImageUri: ${e.message}")
            }
        }
    }
    fun classify(photoProfileFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileRequestBody =
                    photoProfileFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val fileFieldName = "image"
                val filePart = MultipartBody.Part.createFormData(
                    fileFieldName,
                    photoProfileFile.name,
                    fileRequestBody
                )
                val file = filePart

                repository.classify(image = file).catch { error ->
                    _predictResultUiState.value = UiState.Error("error: ${error.message}")
                    eventChannel.send(MyEvent.MessageEvent("error: ${error.message}"))
                }.collect { result ->
                    _predictResultUiState.value = UiState.Success(result.predictedLabel)
                }
            } catch (e: Exception) {
                _predictResultUiState.value = UiState.Error("error: ${e.message}")
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }
}