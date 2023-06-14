package com.bangkit.ecoease.config

import com.bangkit.ecoease.data.remote.interfaces.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        private val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        private val retrofit = Retrofit.Builder().apply {
            baseUrl("https://test-ecoaes.et.r.appspot.com/api/v1/")
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()

        private val fcmRetrofit = Retrofit.Builder().apply {
            baseUrl("https://fcm.googleapis.com/")
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()


        private val mlRetrofit = Retrofit.Builder().apply {
            baseUrl("https://eco-jushlewoya-uc.a.run.app/")
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()

        fun getGarbageApiService(): GarbageApiService =
            retrofit.create(GarbageApiService::class.java)

        fun getUserApiService(): UserApiService = retrofit.create(UserApiService::class.java)
        fun getAddressApiService(): AddressApiService =
            retrofit.create(AddressApiService::class.java)

        fun getOrderApiService(): OrderApiService = retrofit.create(OrderApiService::class.java)
        fun getChatroomApiService(): ChatroomApiService =
            retrofit.create(ChatroomApiService::class.java)

        fun getFCMServerApiService(): FCMServerApiService =
            retrofit.create(FCMServerApiService::class.java)

        fun getFCMClientApiService(): FCMClientApiService =
            fcmRetrofit.create(FCMClientApiService::class.java)

        fun getMLApiService(): MLApiService = mlRetrofit.create(MLApiService::class.java)
    }
}