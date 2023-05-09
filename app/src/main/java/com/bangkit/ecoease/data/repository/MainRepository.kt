package com.bangkit.ecoease.data.repository

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MainRepository {
    private var capturedImageUri: Uri? = null

    fun setCapturedImageUri(uri: Uri){
        capturedImageUri = uri
    }

    fun getCapturedImageUri(): Flow<Uri> {
        Log.d(MainRepository::class.java.simpleName, "getCapturedImageUri: $capturedImageUri")
        return flowOf(capturedImageUri!!)
    }

    companion object{
        @Volatile
        private var INSTANCE: MainRepository? = null

        fun getInstance(): MainRepository = INSTANCE ?: synchronized(this){
            MainRepository().apply {
                INSTANCE = this
            }
        }
    }
}