package com.bangkit.ecoease.data.model.request

data class OrderWithDetail(
	val detailTransactions: List<DetailTransactionsItem>,
	val location: Location,
	val order: Order
)

data class Order(
	val totalTransaction: Int,
	val userId: String,
	val addressId: String,
	val status: String
)

data class Location(
	val latitude: Any,
	val longitude: Any
)

data class DetailTransactionsItem(
	val garbageId: String,
	val total: Int,
	val qty: Int
)

