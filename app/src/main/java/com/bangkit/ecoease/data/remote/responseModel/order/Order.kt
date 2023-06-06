package com.bangkit.ecoease.data.remote.responseModel.order


data class Order(
    val id: String,
    val userId: String,
    val status: String,
    val totalTransaction: Int,
    val addressId: String,
    val locationId: String,
    val createdAt: String,
    val updatedAt: String
)