package com.bangkit.ecoease.data.model.request

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class Register(
    val photoFile: MultipartBody.Part,
    val firstName: RequestBody,
    val lastName: RequestBody,
    val email: RequestBody,
    val password: RequestBody,
    val phoneNumber: RequestBody,
)
