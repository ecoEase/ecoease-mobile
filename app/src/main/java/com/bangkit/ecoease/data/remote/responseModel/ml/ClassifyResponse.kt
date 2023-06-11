package com.bangkit.ecoease.data.remote.responseModel.ml

import com.google.gson.annotations.SerializedName

data class ClassifyResponse(
	@field:SerializedName("image_hex")
	val imageHex: String,

	@field:SerializedName("predicted_label")
	val predictedLabel: String
)
