package com.bangkit.ecoease.data.model

import com.bangkit.ecoease.ui.component.StatusItemHistory

data class OrderHistory(
    val id: String,
    val items: List<String>,
    val date: String,
    val price: Int,
    val status: StatusItemHistory
)
