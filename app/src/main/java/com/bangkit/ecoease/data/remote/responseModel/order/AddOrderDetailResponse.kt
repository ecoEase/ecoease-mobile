package com.bangkit.ecoease.data.remote.responseModel.order

import com.bangkit.ecoease.data.model.request.DetailTransactionsItem

data class AddOrderDetailResponse(
	val data: AddOrderDetailData? = null,
	val message: String
)

data class AddOrderDetailData(
	val detailTransactions: List<DetailTransactionsItem>,
	val order: Order,
	val location: Location? = null,
)

