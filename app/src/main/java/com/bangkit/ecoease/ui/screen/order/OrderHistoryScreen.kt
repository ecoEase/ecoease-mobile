package com.bangkit.ecoease.ui.screen.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.ui.component.ItemHistory
import com.bangkit.ecoease.ui.component.StatusItemHistory

val listItemHistory = listOf(
    OrderHistory(
        items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
        price = 12000,
        date = "20-02-2020",
        statusItemHistory = StatusItemHistory.ON_PROCESS
    ),OrderHistory(
        items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
        price = 12000,
        date = "20-02-2020",
        statusItemHistory = StatusItemHistory.TAKEN
    ),OrderHistory(
        items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
        price = 12000,
        date = "20-02-2020",
        statusItemHistory = StatusItemHistory.NOT_TAKEN
    ),OrderHistory(
        items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
        price = 12000,
        date = "20-02-2020",
        statusItemHistory = StatusItemHistory.CANCELED
    ),OrderHistory(
        items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
        price = 12000,
        date = "20-02-2020",
        statusItemHistory = StatusItemHistory.ON_PROCESS
    ),OrderHistory(
        items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
        price = 12000,
        date = "20-02-2020",
        statusItemHistory = StatusItemHistory.TAKEN
    ),OrderHistory(
        items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
        price = 12000,
        date = "20-02-2020",
        statusItemHistory = StatusItemHistory.NOT_TAKEN
    ),OrderHistory(
        items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
        price = 12000,
        date = "20-02-2020",
        statusItemHistory = StatusItemHistory.CANCELED
    ),
)

@Composable
fun OrderHistoryScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
        ,
        contentPadding = PaddingValues(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(listItemHistory){ item ->
            ItemHistory(items = item.items, date = item.date , totalPrice = item.price.toString(), statusItemHistory = item.statusItemHistory)
        }
    }
}