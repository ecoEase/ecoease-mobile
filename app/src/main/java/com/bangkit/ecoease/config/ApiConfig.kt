package com.bangkit.ecoease.config

import com.bangkit.ecoease.data.remote.interfaces.GarbageApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class ApiConfig{
    companion object{
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder().apply {
            baseUrl("https://34f4-2a09-bac1-34e0-50-00-277-82.ngrok-free.app")// TODO: add api base url
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()

        fun getGarbageApiService(): GarbageApiService = retrofit.create(GarbageApiService::class.java)
    }
}