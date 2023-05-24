package com.bangkit.ecoease.data.model

data class Order(
    val garbageList: List<GarbageAdded?>,
    val total: Int
)
data class GarbageAdded(
    val garbage: com.bangkit.ecoease.data.room.model.Garbage,
    val amount: Int,
    val totalPrice: Int
)