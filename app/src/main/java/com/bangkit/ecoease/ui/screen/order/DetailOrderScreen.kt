package com.bangkit.ecoease.ui.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.room.model.Garbage
import com.bangkit.ecoease.data.room.model.Order
import com.bangkit.ecoease.data.room.model.OrderWithGarbage
import com.bangkit.ecoease.data.room.model.StatusOrderItem
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.*
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DetailOrderScreen(
    navHostController: NavHostController,
    orderId: String,
    onLoadDetailOrder: (String) -> Unit,
    orderDetailStateFlow: StateFlow<UiState<OrderWithGarbage>>,
    onReloadDetailOrder: () -> Unit,
    onUpdateOrderStatus: (Order, StatusOrderItem) -> Unit,
    updateOrderStatusState: StateFlow<UiState<Boolean>>,
    modifier: Modifier = Modifier,
){
    var onUpdate by rememberSaveable{
        mutableStateOf(false)
    }

    val garbages = listOf<GarbageAdded>(
        GarbageAdded(
            garbage = com.bangkit.ecoease.data.room.model.Garbage(id = generateUUID(), imageUrl = "", name = "kaleng", price = 700),
            amount = 2,
            totalPrice = 1400
        )
    )
    orderDetailStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when(uiState){
            is UiState.Loading -> {
                CircularProgressIndicator()
                onLoadDetailOrder(orderId)
            }
            is UiState.Success-> {
                OrderDetailContent(listGarbage = uiState.data.garbage, onUpdateOrderStatus = onUpdateOrderStatus, order = uiState.data.order)
            }
            is UiState.Error -> {
                ErrorHandler(errorText = uiState.errorMessage, onReload = {
                    onReloadDetailOrder()
                })
            }
        }
    }
}

@Composable
fun OrderDetailContent(
    order: Order,
    onUpdateOrderStatus: (Order, StatusOrderItem) -> Unit,
    listGarbage: List<Garbage>,
    modifier: Modifier = Modifier,
){
    var openDialog by remember{
        mutableStateOf(false)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(vertical = 32.dp)
    ) {
        Text(text = stringResource(R.string.status), style = MaterialTheme.typography.body1.copy(
            color = DarkGrey
        ))
        StatusOrder(statusItemHistory = order.status)
        Text(text = stringResource(R.string.address_info), style = MaterialTheme.typography.body1.copy(
            color = DarkGrey
        ))
        DetailAddressCard(name = order.addressId, detail = "jalan yg lurus", city = "Tulungagung")
        Box(modifier = Modifier.height(30.dp))
        Text(text = stringResource(R.string.detail), style = MaterialTheme.typography.body1.copy(
            color = DarkGrey
        ))
        LazyColumn(modifier = Modifier.weight(1f)){
            items(listGarbage){
                DetailCardGarbage(garbageName = it.name, amount = 1, price = it.price, total = 100)
            }
        }
        if(order.status == StatusOrderItem.NOT_TAKEN){
            RoundedButton(
                text = "batalkan pesanan",
                type = RoundedButtonType.SECONDARY,
                enabled = true,
                onClick = {
                    openDialog = true
    //                onUpdateOrderStatus()
                },
                modifier = Modifier.fillMaxWidth()
            )
            DialogBox(text = "Apakah anda yakin untuk membatalkan pesanan anda?", onDissmiss = { openDialog = false }, isOpen = openDialog, onAccept = { onUpdateOrderStatus(order, StatusOrderItem.CANCELED) })
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DetailOrderScreenPreview(){
//    EcoEaseTheme() {
//        DetailOrderScreen(
//            rememberNavController(),
//            orderId = "",
//            {},
//            {},
//            MutableStateFlow(UiState.Loading),
//        )
//    }
//}