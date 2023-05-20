package com.bangkit.ecoease.data.repository

import android.util.Log
import com.bangkit.ecoease.data.datastore.DataStorePreferences
import com.bangkit.ecoease.data.dummy.OrderHistoryDummy
import com.bangkit.ecoease.data.dummy.listGarbage
import com.bangkit.ecoease.data.model.Garbage
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.model.OrderHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class MainRepository(private val datastore: DataStorePreferences) {
    private var capturedImageUri: ImageCaptured? = null
    //CAMERA
    fun setCapturedImage(imageCapture: ImageCaptured){
        capturedImageUri = imageCapture
    }
    fun getCapturedImage(): Flow<ImageCaptured> {
        Log.d(MainRepository::class.java.simpleName, "getCapturedImageUri: $capturedImageUri")
        return flowOf(capturedImageUri!!)
    }
    //ON BOARDING
    suspend fun getIsFinishOnboard(): Boolean = datastore.isFinishReadOnBoard().first()
    suspend fun finishOnBoard(){
        datastore.finishReadOnboard()
    }
    //AUTH
    suspend fun getToken(): String = datastore.getAuthToken().first()
    suspend fun setToken(newToken: String){
        datastore.setToken(newToken)
    }
    //GARBAGE
    fun getAllGarbage(): Flow<List<Garbage>> = flowOf(listGarbage)
    //ORDER HISTORY
    fun getAllOrderHistories(): Flow<List<OrderHistory>> = flowOf(OrderHistoryDummy.getOrderHistories())

    companion object{
        @Volatile
        private var INSTANCE: MainRepository? = null

        fun getInstance(datastore: DataStorePreferences): MainRepository = INSTANCE ?: synchronized(this){
            MainRepository(datastore).apply {
                INSTANCE = this
            }
        }
    }
}