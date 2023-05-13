package com.bangkit.ecoease.data.repository

import android.util.Log
import com.bangkit.ecoease.data.model.ImageCaptured
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MainRepository {
    private var capturedImageUri: ImageCaptured? = null

    fun setCapturedImage(imageCapture: ImageCaptured){
        capturedImageUri = imageCapture
    }

    fun getCapturedImage(): Flow<ImageCaptured> {
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