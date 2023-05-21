package com.bangkit.ecoease.data.repository

import android.util.Log
import com.bangkit.ecoease.data.datastore.DataStorePreferences
import com.bangkit.ecoease.data.dummy.AddressDummy
import com.bangkit.ecoease.data.dummy.GarbageDummy
import com.bangkit.ecoease.data.dummy.OrderHistoryDummy
import com.bangkit.ecoease.data.room.model.Garbage
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.data.room.model.Address
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
    suspend fun getAllGarbage(): Flow<List<Garbage>>{
        try {
            val response = GarbageDummy.listGarbage
            roomDatabase.garbageDao().deleteAllGarbage()
            response.forEach { garbage ->
                roomDatabase.garbageDao().addGarbage(garbage)
            }
        }catch (e: Exception){
            if (roomDatabase.garbageDao().getAllGarbage().isEmpty()){
                Log.d(TAG, "getAllGarbage: e")
                throw e
            }
        }
        return flowOf(roomDatabase.garbageDao().getAllGarbage())
    }
    //ORDER HISTORY
    fun getAllOrderHistories(): Flow<List<OrderHistory>> = flowOf(OrderHistoryDummy.getOrderHistories())

    //Address
    suspend fun getSavedAddress(): Flow<List<Address>>{
        try {
           val response = AddressDummy.listSavedAddress
           roomDatabase.addressDao().deleteAllAddress()
           response.forEach { address ->
               roomDatabase.addressDao().addAddress(address)
           }
        }catch (e: Exception){
            Log.d(TAG, "getSavedAddress: ${e.message}")
            if(roomDatabase.addressDao().getAllAddress().isEmpty()){
                throw e
            }
        }
        return flowOf(roomDatabase.addressDao().getAllAddress())
    }
    suspend fun addAddress(address: Address){
//        roomDatabase.addressDao().addAddress(address)
        AddressDummy.listSavedAddress.add(address)
    }
    suspend fun deleteAddress(address: Address){
//        roomDatabase.addressDao().deleteAddress(address)
        AddressDummy.listSavedAddress.remove(address)
    }

    companion object{
        val TAG = MainRepository::class.java.simpleName

        @Volatile
        private var INSTANCE: MainRepository? = null

        fun getInstance(datastore: DataStorePreferences, roomDatabase: MainDatabase): MainRepository = INSTANCE ?: synchronized(this){
            MainRepository(datastore, roomDatabase).apply {
                INSTANCE = this
            }
        }
    }
}