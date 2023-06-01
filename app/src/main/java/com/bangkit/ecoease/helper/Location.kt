package com.bangkit.ecoease.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

fun FusedLocationProviderClient.getLastLocation(context: Context, onSuccess: (Location) -> Unit){
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        this.lastLocation.addOnSuccessListener { location ->
            if(location == null) throw NullPointerException("Location is null!")
            onSuccess(location)
        }
    }else {
        throw SecurityException("Location permission not granted!")
    }
}