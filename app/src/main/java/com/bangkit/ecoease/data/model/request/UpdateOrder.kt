package com.bangkit.ecoease.data.model.request

import com.bangkit.ecoease.data.room.model.StatusOrderItem

data class UpdateOrder(
    val id: String,
    val status: StatusOrderItem,
    val mitraId: String? = null,
)
