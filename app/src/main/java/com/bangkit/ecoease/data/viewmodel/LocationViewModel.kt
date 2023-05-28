package com.bangkit.ecoease.data.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.helper.getLastLocation
import com.bangkit.ecoease.ui.common.UiState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel(private val repository: MainRepository) : ViewModel() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(repository.context)
    private val _lastLocationStateFlow: MutableStateFlow<UiState<Location>> = MutableStateFlow(UiState.Loading)
    val lastLocationStateFlow: StateFlow<UiState<Location>> = _lastLocationStateFlow

    // TODO: fix last location 
    fun getLastLocation(){
        if (ActivityCompat.checkSelfPermission(
                repository.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                repository.context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if(location == null) _lastLocationStateFlow.value = UiState.Error("Location is null!")
                else  _lastLocationStateFlow.value = UiState.Success(location)
            }
        }else {
            _lastLocationStateFlow.value = UiState.Error("Location permission not granted!")
        }
    }
}