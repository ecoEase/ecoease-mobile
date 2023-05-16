package com.bangkit.ecoease.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    modifier: Modifier = Modifier
){
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(singapore, 15f)
    }
    Box(modifier = modifier.fillMaxSize()){
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ){
            Marker(
                state = MarkerState(position = singapore),
                title = "Singapore",
                snippet = "marker in singapore"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview(){
    EcoEaseTheme {
        MapScreen()
    }
}