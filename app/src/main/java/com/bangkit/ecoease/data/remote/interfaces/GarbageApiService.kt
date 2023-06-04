package com.bangkit.ecoease.data.remote.interfaces

import com.bangkit.ecoease.data.remote.responseModel.GarbageResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface GarbageApiService{
    @GET("garbage")
    suspend fun get(@Header("Authorization") token: String): GarbageResponse
}