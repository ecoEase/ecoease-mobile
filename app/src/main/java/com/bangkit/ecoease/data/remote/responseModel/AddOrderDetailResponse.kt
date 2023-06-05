package com.bangkit.ecoease.data.remote.responseModel

data class AddOrderDetailResponse(
	val data: Data? = null,
	val message: String? = null
)

data class DetailTransactionsItem(
	val garbageId: String? = null,
	val createdAt: String? = null,
	val total: Int? = null,
	val qty: Int? = null,
	val orderId: String? = null,
	val updatedAt: String? = null
)

data class Data(
	val detailTransactions: List<DetailTransactionsItem?>? = null,
	val location: Location? = null,
	val order: Order? = null
)

data class Location(
	val latitude: Int? = null,
	val longitude: Int? = null
)

data class Order(
	val createdAt: String? = null,
	val totalTransaction: Int? = null,
	val userId: String? = null,
	val addressId: String? = null,
	val id: String? = null,
	val locationId: String? = null,
	val status: String? = null,
	val updatedAt: String? = null
)

