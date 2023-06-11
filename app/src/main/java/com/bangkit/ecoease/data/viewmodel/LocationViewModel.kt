package com.bangkit.ecoease.data.viewmodel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.event.MyEvent
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.helper.getLastLocation
import com.bangkit.ecoease.ui.common.UiState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: MainRepository) : ViewModel() {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(repository.context)
    private val _lastLocationStateFlow: MutableStateFlow<UiState<Location>> =
        MutableStateFlow(UiState.Loading)
    val lastLocationStateFlow: StateFlow<UiState<Location>> = _lastLocationStateFlow
    private val eventChannel = Channel<MyEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun getLastLocation() {
        try {
            fusedLocationClient.getLastLocation(
                context = repository.context,
                onSuccess = { location ->
                    _lastLocationStateFlow.value = UiState.Success(location)
                },
                onError = { error ->
                    _lastLocationStateFlow.value = UiState.Error("error: $error")
                    viewModelScope.launch {
                        eventChannel.send(MyEvent.MessageEvent("error: $error"))
                    }
                }
            )
        } catch (e: Exception) {
            _lastLocationStateFlow.value = UiState.Error("error: ${e.message}")
            viewModelScope.launch {
                eventChannel.send(MyEvent.MessageEvent("error: $e"))
            }
        }
    }

    fun reloadLastLocation() {
        _lastLocationStateFlow.value = UiState.Loading
    }
}