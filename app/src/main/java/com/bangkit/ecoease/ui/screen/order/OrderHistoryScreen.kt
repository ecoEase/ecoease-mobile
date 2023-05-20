package com.bangkit.ecoease.ui.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.ItemHistory
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OrderHistoryScreen(
    orderHistoryState: StateFlow<UiState<List<OrderHistory>>>,
    loadOrderHistory: () -> Unit,
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
                        ItemHistory(items = item.items, date = item.date , totalPrice = item.price.toString(), statusItemHistory = item.status, onClickDetail = { navHostController.navigate(Screen.DetailOrder.route) })
                    }
                }
            }
            is UiState.Error -> Text(text = uiState.errorMessage)
        }
    }
}

@Composable
private fun Loader(modifier: Modifier = Modifier){
    Row(modifier = Modifier.fillMaxWidth()) {
        CircularProgressIndicator(modifier = modifier.align(Alignment.CenterVertically))
    }
}
