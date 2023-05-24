package com.bangkit.ecoease.ui.screen.order

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.room.model.OrderWithGarbage
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.ErrorHandler
import com.bangkit.ecoease.ui.component.ItemHistory
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OrderHistoryScreen(
    orderHistoryState: StateFlow<UiState<List<OrderWithGarbage>>>,
    loadOrderHistory: () -> Unit,
    reloadOrderHistory: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    orderHistoryState.collectAsState(initial = UiState.Loading).value.let {uiState ->
        when(uiState){
            is UiState.Loading -> {
                Loader()
                loadOrderHistory()
            }
            is UiState.Success -> { 
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                    ,
                    contentPadding = PaddingValues(top = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    items(uiState.data){ item ->
                        Log.d("order history screen", "OrderHistoryScreen: $item")
                        ItemHistory(items = item.garbage.map { it.name }, date = item.order.created , totalPrice = item.order.totalTransaction.toString(), statusItemHistory = item.order.status, onClickDetail = { navHostController.navigate(
                            Screen.DetailOrder.route)
                        })
                    }
                }
            }
            is UiState.Error -> ErrorHandler(
                errorText = uiState.errorMessage,
                onReload = { reloadOrderHistory() })
        }
    }
}

@Composable
private fun Loader(modifier: Modifier = Modifier){
    Row(modifier = Modifier.fillMaxWidth()) {
        CircularProgressIndicator(modifier = modifier.align(Alignment.CenterVertically))
    }
}
