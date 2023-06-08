package com.bangkit.ecoease.data.remote.interfaces

import com.bangkit.ecoease.data.model.request.FCMNotification
import com.bangkit.ecoease.data.remote.responseModel.fcm.FCMResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FCMClientApiService{
    @POST("fcm/send")
    suspend fun sendNotification(
        @Header("Authorization") token: String,
        @Body body: FCMNotification
    ) : FCMResponse
}