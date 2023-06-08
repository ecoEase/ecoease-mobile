package com.bangkit.ecoease.data.remote.interfaces

import com.bangkit.ecoease.data.model.request.UpdateFCMToken
import com.bangkit.ecoease.data.remote.responseModel.fcm.UpdateFCMTokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface FCMServerApiService{
    @PUT("fcm/mitra/{id}")
    suspend fun updateMitraToken(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: UpdateFCMToken
    ) : UpdateFCMTokenResponse

    @PUT("fcm/user/{id}")
    suspend fun updateUserToken(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: UpdateFCMToken
    ) : UpdateFCMTokenResponse
}