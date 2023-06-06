package com.bangkit.ecoease.data.remote.interfaces

import com.bangkit.ecoease.data.model.request.OrderWithDetail
import com.bangkit.ecoease.data.remote.responseModel.order.AddOrderDetailResponse
import com.bangkit.ecoease.data.remote.responseModel.order.OrderDetailResponse
import com.bangkit.ecoease.data.remote.responseModel.order.OrderResponse
import retrofit2.http.*

interface OrderApiService{
    @GET("orders")
    suspend fun getAll(@Header("Authorization") token: String): OrderResponse

    @GET("orders")
    suspend fun getByUser(@Header("Authorization") token: String, @Query("userId") userId: String): OrderResponse

    @GET("orders")
    suspend fun getByMitra(@Header("Authorization") token: String, @Query("mitraId") mitraId: String): OrderResponse

    @GET("orders")
    suspend fun getById(@Header("Authorization") token: String, @Query("id") id: String): OrderDetailResponse

    @POST("orders/with-detail-transactions")
    suspend fun addNewOrder(@Header("Authorization") token: String, @Body body: OrderWithDetail): AddOrderDetailResponse
}
