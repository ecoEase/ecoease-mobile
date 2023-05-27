package com.bangkit.ecoease.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.room.model.Garbage
import com.bangkit.ecoease.helper.toCurrency
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.Banner
import com.bangkit.ecoease.ui.component.CardPrice
import com.bangkit.ecoease.ui.component.ErrorHandler
import com.bangkit.ecoease.utils.WindowInfo
import com.bangkit.ecoease.utils.rememberWindowInfo
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DashboardScreen(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    onLoadGarbage: () -> Unit,
    onReloadGarbage: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    val windowInfo = rememberWindowInfo()

    if(windowInfo.screenWidthInfo == WindowInfo.WindowType.Compact){
        DashboardScreenPotraitContent(
            garbageStateFlow = garbageStateFlow,
            onLoadGarbage = onLoadGarbage,
            onReloadGarbage = onReloadGarbage,
            navHostController = navHostController,
            modifier = modifier
        )
    }else{
        DashboardScreenLandscapeContent(
            garbageStateFlow = garbageStateFlow,
            onLoadGarbage = onLoadGarbage,
            onReloadGarbage = onReloadGarbage,
            navHostController = navHostController,
            modifier = modifier
        )
    }
}

@Composable
fun DashboardScreenPotraitContent(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    onLoadGarbage: () -> Unit,
    onReloadGarbage: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 32.dp)
    ) {
        Header(navHostController)
        DashboardScreenContent(
            garbageStateFlow = garbageStateFlow,
            onLoadGarbage = onLoadGarbage,
            onReloadGarbage = onReloadGarbage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


@Composable
fun DashboardScreenLandscapeContent(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    onLoadGarbage: () -> Unit,
    onReloadGarbage: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header(navHostController = navHostController, modifier = Modifier.weight(1f))
        DashboardScreenContent(
            garbageStateFlow = garbageStateFlow,
            onLoadGarbage = onLoadGarbage,
            onReloadGarbage = onReloadGarbage,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun Header(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
){
    Column(modifier) {
        Text(text = stringResource(R.string.good_morning), style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(42.dp))
        Banner(bannerAction = { navHostController.navigate(Screen.Order.route) })
        Spacer(modifier = Modifier.height(42.dp))
    }
}
@Composable
fun DashboardScreenContent(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    onLoadGarbage: () -> Unit,
    onReloadGarbage: () -> Unit,
    modifier: Modifier = Modifier,
){
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.garbage_price), style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        garbageStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when(uiState){
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = modifier)
                    onLoadGarbage()
                }
                is UiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 64.dp)
                    ){
                        items(uiState.data){item ->
                            CardPrice(imageUrl = item.imageUrl, name = item.name, price = "Rp${item.price.toCurrency()}")
                        }
                    }
                }
                is UiState.Error -> ErrorHandler(errorText = uiState.errorMessage, onReload = { onReloadGarbage() })
            }
        }
    }
}