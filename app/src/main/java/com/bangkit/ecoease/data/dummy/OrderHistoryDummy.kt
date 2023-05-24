package com.bangkit.ecoease.data.dummy

import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.data.room.model.StatusOrderItem
import com.bangkit.ecoease.helper.generateUUID

object OrderHistoryDummy{
    private val listItemHistory = listOf(
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusOrderItem.ON_PROCESS
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusOrderItem.TAKEN
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusOrderItem.NOT_TAKEN
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusOrderItem.CANCELED
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusOrderItem.ON_PROCESS
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusOrderItem.TAKEN
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusOrderItem.NOT_TAKEN
        ),
        OrderHistory(
            id = generateUUID(),
            items = listOf("Kantong plastik", "Botol plastik", "Karton", "Kertas"),
            price = 12000,
            date = "20-02-2020",
            status = StatusOrderItem.CANCELED
        ),
    )
    fun getOrderHistories(): List<OrderHistory> = listItemHistory
}