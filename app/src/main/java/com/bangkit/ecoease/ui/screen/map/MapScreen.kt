package com.bangkit.ecoease.ui.screen

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.room.model.OrderWithDetailTransaction
import com.bangkit.ecoease.helper.getLastLocation
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.RoundedButton
import com.bangkit.ecoease.ui.screen.order.locationPermissions
import com.bangkit.ecoease.ui.screen.order.locationPermissions28Above
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    navHostController: NavHostController,
    availableOrderStateFlow: StateFlow<UiState<List<OrderWithDetailTransaction>>>,
    loadAvailableOrders: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()

    val permissionsState =
        rememberMultiplePermissionsState(permissions = if (Build.VERSION.SDK_INT > 28) locationPermissions28Above else locationPermissions)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var userName by rememberSaveable { mutableStateOf("") }
    var id: String by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var detailAddress by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var garbageNames by remember{ mutableStateOf("") }

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
        fusedLocationClient.getLastLocation(context,
            onSuccess = {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
            },
            onError = {
                Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
            }
        )
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    fun poiClickHandler(data: OrderWithDetailTransaction) {
        data.let {
            id = it.order.id
            userName = it.user.firstName
            detailAddress = it.address.detail
            district = it.address.district
            city = it.address.city
            garbageNames = it.items.map { it.garbage.type }.joinToString(", ")
        }
        bottomSheetScaffoldState.bottomSheetState.isExpanded
    }
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
           DetailOrder(
                id = id,
                userName = userName,
                date = date,
                detailAddress = detailAddress,
                district = district,
                city = city,
                garbageNames = garbageNames,
                showCollapseButton = bottomSheetScaffoldState.bottomSheetState.isExpanded,
                collapseBottomSheet = {
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                    }
                },
                openDetailOrder = { id -> navHostController.navigate(Screen.DetailOrder.createRoute(id)) }
            )
        },
        sheetPeekHeight = if(id.isEmpty()) 0.dp else 64.dp,
        sheetShape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
    ) {
        Box(modifier = modifier
            .fillMaxSize()) {
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
                            uiState.data.forEach {
                                it.location?.let { location ->
                                    Marker(
                                        state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                                        onClick = { _ ->
                                            if(bottomSheetScaffoldState.bottomSheetState.isCollapsed){
                                                coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
                                            }
                                            poiClickHandler(it)
                                            false
                                        },
                                        title = it.address.city,
                                        snippet = "marker in singapore"
                                    )
                                }
                            }
                        }
                        is UiState.Error -> Log.d("TAG", "MapScreen: ${uiState.errorMessage}")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailOrder(
    id: String,
    userName: String,
    date: String,
    detailAddress: String,
    district: String,
    city: String,
    garbageNames: String,
    showCollapseButton: Boolean,
    collapseBottomSheet: () -> Unit,
    openDetailOrder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 64.dp, top = 16.dp)
            .padding(horizontal = 32.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)) {
                Box(modifier = Modifier
                    .background(MaterialTheme.colors.onBackground)
                    .width(64.dp)
                    .height(4.dp)
                    .align(Alignment.Center))
                if(showCollapseButton) IconButton(onClick = { collapseBottomSheet() }, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "close icon")
                }
            }
            Spacer(modifier = Modifier.height(64.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = garbageNames,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Text(
                    text = date, style = MaterialTheme.typography.caption.copy(
                        DarkGrey
                    )
                )
            }
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.caption.copy(color = DarkGrey)
                    )
                    Text(text = detailAddress, style = MaterialTheme.typography.subtitle2)
                    Text(text = "$district, $city.", style = MaterialTheme.typography.caption)
                }
                RoundedButton(text = stringResource(id = R.string.detail), modifier = Modifier.align(Alignment.Bottom), onClick = { openDetailOrder(id) })
            }
        }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    EcoEaseTheme {
//        MapScreen()
    }
}