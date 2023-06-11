package com.bangkit.ecoease.data.remote.interfaces

import com.bangkit.ecoease.data.model.request.Login
import com.bangkit.ecoease.data.remote.responseModel.RegisterResponse
import com.bangkit.ecoease.data.remote.responseModel.UserLoginResponse
import com.bangkit.ecoease.data.remote.responseModel.ml.ClassifyResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MLApiService{
    @Multipart
    @POST("classify")
    suspend fun classify(
        @Part image: MultipartBody.Part,
    ) : ClassifyResponse
}