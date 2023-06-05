package com.bangkit.ecoease.config

import com.bangkit.ecoease.data.remote.interfaces.AddressApiService
import com.bangkit.ecoease.data.remote.interfaces.GarbageApiService
import com.bangkit.ecoease.data.remote.interfaces.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig{
    companion object{
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder().apply {
            baseUrl("https://895f-2a09-bac5-3a19-137d-00-1f1-213.ngrok-free.app/api/v1/")// TODO: add api base url
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()

        fun getGarbageApiService(): GarbageApiService = retrofit.create(GarbageApiService::class.java)
        fun getUserApiService(): UserApiService = retrofit.create(UserApiService::class.java)
        fun getAddressApiService(): AddressApiService = retrofit.create(AddressApiService::class.java)
    }
}