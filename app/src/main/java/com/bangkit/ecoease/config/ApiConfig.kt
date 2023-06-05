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
        private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        private val retrofit = Retrofit.Builder().apply {
            baseUrl("https://125e-2a09-bac5-3a1e-88c-00-da-32.ngrok-free.app/api/v1/")// TODO: add api base url
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()

        fun getGarbageApiService(): GarbageApiService = retrofit.create(GarbageApiService::class.java)
        fun getUserApiService(): UserApiService = retrofit.create(UserApiService::class.java)
        fun getAddressApiService(): AddressApiService = retrofit.create(AddressApiService::class.java)
    }
}