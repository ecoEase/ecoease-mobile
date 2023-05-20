package com.bangkit.ecoease.data.repository

import android.util.Log
import androidx.room.RoomDatabase
import com.bangkit.ecoease.data.datastore.DataStorePreferences
import com.bangkit.ecoease.data.dummy.OrderHistoryDummy
import com.bangkit.ecoease.data.dummy.listGarbage
import com.bangkit.ecoease.data.model.Garbage
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.data.room.dao.Address
import com.bangkit.ecoease.data.room.database.MainDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class MainRepository(private val datastore: DataStorePreferences, private val roomDatabase: MainDatabase) {
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

    //Address
    fun getSavedAddress(): Flow<List<Address>> = flowOf(roomDatabase.addressDao().getAllAddress())
    suspend fun addAddress(address: Address) = roomDatabase.addressDao().addAddress(address)
    suspend fun deleteAddress(address: Address) = roomDatabase.addressDao().deleteAddress(address)

    companion object{
        @Volatile
        private var INSTANCE: MainRepository? = null

        fun getInstance(datastore: DataStorePreferences, roomDatabase: MainDatabase): MainRepository = INSTANCE ?: synchronized(this){
            MainRepository(datastore, roomDatabase).apply {
                INSTANCE = this
            }
        }
    }
}