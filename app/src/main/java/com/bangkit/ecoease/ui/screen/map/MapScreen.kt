package com.bangkit.ecoease.ui.screen

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.room.model.OrderWithDetailTransaction
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.screen.order.locationPermissions
import com.bangkit.ecoease.ui.screen.order.locationPermissions28Above
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    availableOrderStateFlow: StateFlow<UiState<List<OrderWithDetailTransaction>>>,
    loadAvailableOrders: () -> Unit = {},
    modifier: Modifier = Modifier
){
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(singapore, 15f)
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = if(Build.VERSION.SDK_INT > 28) locationPermissions28Above else locationPermissions)
    PermissionsRequired(
        multiplePermissionsState = permissionsState,
        permissionsNotGrantedContent = { /* ... */ },
        permissionsNotAvailableContent = { /* ... */ }
    ){
        Box(modifier = modifier.fillMaxSize()){
            GoogleMap(
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    compassEnabled = true
                ),
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ){
                availableOrderStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
                    when(uiState){
                        is UiState.Loading -> loadAvailableOrders()
                        is UiState.Success -> {
                            Log.d("TAG", "MapScreen: ${uiState.data}")
//                            LazyColumn {
//                                items(uiState.data, key = {it.order.id}){
                                    uiState.data.forEach {
                                        it.location?.let { location ->
                                            Marker(
                                                state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                                                title = it.address.city,
                                                snippet = "marker in singapore"
                                            )
                                        }
                                    }
//                                }
//                            }
                        }
                        is UiState.Error -> Text(text = uiState.errorMessage)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview(){
    EcoEaseTheme {
//        MapScreen()
    }
}