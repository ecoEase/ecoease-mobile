package com.bangkit.ecoease.data.remote.responseModel

data class OrderDetailResponse(
	val data: Data? = null,
	val message: String
)

data class Data(
	val totalTransaction: Int,
	val address: Address,
	val user: User,
	val mitraId: String? = null,
	val addressId: String,
	val locationId: String,
	val garbages: List<GarbagesItem>,
	val createdAt: String,
	val userId: String,
	val mitra: Any?,
	val location: Location,
	val id: String,
	val status: String,
	val updatedAt: String
)

data class User(
	val firstName: String,
	val lastName: String,
	val createdAt: String,
	val password: String,
	val urlPhotoProfile: String,
	val phoneNumber: String,
	val id: String,
	val email: String,
	val updatedAt: String
)