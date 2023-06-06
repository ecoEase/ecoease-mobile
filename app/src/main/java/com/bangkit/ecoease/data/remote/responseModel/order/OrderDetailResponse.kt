package com.bangkit.ecoease.data.remote.responseModel.order

import com.bangkit.ecoease.data.remote.responseModel.order.Location
import com.bangkit.ecoease.data.remote.responseModel.order.OrderDetailData

data class OrderDetailResponse(
	val data: OrderDetailData? = null,
	val message: String
)
