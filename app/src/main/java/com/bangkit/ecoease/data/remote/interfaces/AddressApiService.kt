package com.bangkit.ecoease.data.remote.interfaces

import com.bangkit.ecoease.data.model.request.Address
import com.bangkit.ecoease.data.remote.responseModel.AddAddressResponse
import com.bangkit.ecoease.data.remote.responseModel.AddressResponse
import retrofit2.http.*

interface AddressApiService {
    @GET("address")
    suspend fun getSavedAddress(@Header("Authorization") token: String, @Query("userId") userId: String): AddressResponse

    @POST("address")
    suspend fun addNewAddress(@Header("Authorization") token: String, @Body address: Address): AddAddressResponse

    @PUT("address/use/{id}")
    suspend fun selectUseAddress(@Header("Authorization") token: String, @Path("id") id: String): AddressResponse
}