package com.bangkit.ecoease.data.dummy

import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.component.StatusItemHistory

object OrderHistoryDummy{
    private val listItemHistory = listOf(
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusItemHistory.ON_PROCESS
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusItemHistory.TAKEN
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusItemHistory.NOT_TAKEN
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusItemHistory.CANCELED
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusItemHistory.ON_PROCESS
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusItemHistory.TAKEN
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusItemHistory.NOT_TAKEN
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusItemHistory.CANCELED
        ),
    )
    fun getOrderHistories(): List<OrderHistory> = listItemHistory
}