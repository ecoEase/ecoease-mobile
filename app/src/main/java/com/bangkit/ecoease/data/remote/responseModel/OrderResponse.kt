package com.bangkit.ecoease.data.remote.responseModel

data class OrderResponse(
	val data: List<DataItem>? = null,
	val message: String
)


// TODO: update mitra datatype
data class DataItem(
	val id: String,
	val totalTransaction: Int,
	val address: AddressItem,
	val user: UserData,
	val addressId: String,
	val garbages: List<GarbagesItem>,
	val createdAt: String,
	val userId: String,
	val mitraId: String? = null,
	val locationId: String? = null,
	val mitra: Any? = null,
	val location: Location? = null,
	val status: String,
	val updatedAt: String
)

data class Location(
	val createdAt: String,
	val latitude: Int,
	val id: String,
	val longitude: Int,
	val updatedAt: String
)

data class Detailtransaction(
	val garbageId: String,
	val createdAt: String,
	val total: Int,
	val qty: Int,
	val orderId: String,
	val updatedAt: String
)

data class GarbagesItem(
	val detailtransaction: Detailtransaction,
	val createdAt: String,
	val price: Int,
	val urlPhoto: String,
	val id: String,
	val type: String,
	val updatedAt: String
)

