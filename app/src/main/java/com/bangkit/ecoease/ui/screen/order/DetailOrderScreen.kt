package com.bangkit.ecoease.ui.screen.order

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.event.MyEvent
import com.bangkit.ecoease.data.room.model.*
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.*
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.ui.theme.OrangeAccent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DetailOrderScreen(
    orderId: String,
    userStateFlow: StateFlow<User?>,
    sendNotification: (token: String, message: String) -> Unit,
    onLoadDetailOrder: (String) -> Unit,
    orderDetailStateFlow: StateFlow<UiState<OrderWithDetailTransaction>>,
    onReloadDetailOrder: () -> Unit,
    onUpdateOrderStatus: (Order, StatusOrderItem) -> Unit,
    eventFlow: Flow<MyEvent>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var userId by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is MyEvent.MessageEvent -> Toast.makeText(
                    context, event.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    orderDetailStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Loading -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                onLoadDetailOrder(orderId)
            }
            is UiState.Success -> {
                OrderDetailContent(
                    listGarbage = uiState.data.items,
                    onUpdateOrderStatus = onUpdateOrderStatus,
                    order = uiState.data.order,
                    address = uiState.data.address,
                    mitra = uiState.data.mitra,
                    sendNotification = sendNotification,
                    modifier = modifier,
                    myId = userStateFlow.collectAsState().value?.id ?: ""
                )
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
    address: Address,
    mitra: Mitra?,
    myId: String,
    sendNotification: (token: String, message: String) -> Unit,
    onUpdateOrderStatus: (Order, StatusOrderItem) -> Unit,
    listGarbage: List<GarbageTransactionWithDetail>,
    modifier: Modifier = Modifier,
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.status), style = MaterialTheme.typography.body1.copy(
                color = DarkGrey
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatusOrder(statusItemHistory = order.status)
            if (order.status == StatusOrderItem.NOT_TAKEN && order.userId == myId) {
                PillWidget(color = OrangeAccent,
                    textColor = Color.White,
                    text = "batalkan pesanan",
                    modifier = Modifier.clickable { openDialog = true })
            }
        }
        Text(
            text = stringResource(R.string.address_info),
            style = MaterialTheme.typography.body1.copy(
                color = DarkGrey
            )
        )
        DetailAddressCard(
            name = address.name,
            detail = address.detail,
            district = address.district,
            city = address.city
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(R.string.detail), style = MaterialTheme.typography.body1.copy(
                    color = DarkGrey
                )
            )
            mitra?.let {
                Column {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(R.string.pick_by),
                        style = MaterialTheme.typography.body1.copy(
                            color = DarkGrey
                        )
                    )
                    Text(text = it.firstName, style = MaterialTheme.typography.body1)
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listGarbage) {
                DetailCardGarbage(
                    garbageName = it.garbage.type,
                    amount = it.orderInfo.qty,
                    price = it.garbage.price,
                    total = it.orderInfo.total
                )
            }
        }

        DialogBox(text = "Apakah anda yakin untuk membatalkan pesanan anda?",
            onDissmiss = { openDialog = false },
            isOpen = openDialog,
            onAccept = {
                onUpdateOrderStatus(order, StatusOrderItem.CANCELED)
                sendNotification(mitra?.fcmToken ?: "", "Pesanan telah dibatalkan oleh user 😢")
            })
    }
}