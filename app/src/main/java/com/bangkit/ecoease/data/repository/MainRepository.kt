package com.bangkit.ecoease.data.repository

import android.util.Log
import androidx.room.ColumnInfo
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
        AddressDummy.listSavedAddress.add(address)//this dummy will simulate data from api
        roomDatabase.addressDao().addAddress(address)
    }
    suspend fun deleteAddress(address: Address){
        AddressDummy.listSavedAddress.remove(address)//this dummy will simulate data from api
        roomDatabase.addressDao().deleteAddress(address)
    }

    suspend fun getSelectedAddress(): Flow<Address?>{
        var response: Flow<Address?>
        try {
            response = flowOf(roomDatabase.addressDao().getSelectedAddress())
        }catch (e: Exception){
            throw e
        }
        return response
    }

    suspend fun saveSelectedAddress(address: Address){
        //call api update selected address

        val updatedAddressStatus = Address(
            id = address.id,
            name = address.name,
            district = address.district,
            city = address.city,
            detail = address.detail,
            selected = true
        )
        //update all saved address selected value to false
        val resetSelectedAddresses = roomDatabase.addressDao().getAllAddress().map { item ->  Address(
                id = item.id,
                name = item.name,
                district = item.district,
                city = item.city,
                detail = item.detail,
                selected = false
            )
        }
        roomDatabase.addressDao().updateBatchAddresses(resetSelectedAddresses)
        roomDatabase.addressDao().updateAddress(updatedAddressStatus)
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