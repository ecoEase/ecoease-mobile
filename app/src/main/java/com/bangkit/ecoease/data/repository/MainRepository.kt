package com.bangkit.ecoease.data.repository

import android.content.Context
import android.util.Log
import com.bangkit.ecoease.data.datastore.DataStorePreferences
import com.bangkit.ecoease.data.dummy.AddressDummy
import com.bangkit.ecoease.data.dummy.GarbageDummy
import com.bangkit.ecoease.data.dummy.UserDummy
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.room.database.MainDatabase
import com.bangkit.ecoease.data.room.model.*
import com.bangkit.ecoease.helper.generateUUID
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class MainRepository(private val datastore: DataStorePreferences, private val roomDatabase: MainDatabase, val context: Context) {
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
    //USER
    suspend fun setUser(){
        try {
            val response = UserDummy.get()
            roomDatabase.userDao().deleteAll()
            roomDatabase.userDao().addUser(response)
        }catch (e: Exception){
            throw e
        }
    }
    suspend fun resetUser(){
        try {
            roomDatabase.userDao().deleteAll()
        }catch (e: Exception){
            throw e
        }
    }
    fun getUser() : Flow<User> = flowOf(roomDatabase.userDao().getUser())
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
    fun getAllOrderHistories(userId: String): Flow<List<OrderWithDetailTransaction>> {
//            flowOf(OrderHistoryDummy.getOrderHistories())
        val orderWithDetailTransaction = roomDatabase.orderDao().getAllOrdersWithTransaction()
        val gson =  Gson()
        val json = gson.toJsonTree(orderWithDetailTransaction)

        Log.d("TAG", "getAllOrderHistories: $json")
        return flowOf(roomDatabase.orderDao().getAllOrderFromUser(userId))
    }
    //Address
    suspend fun getSavedAddress(): Flow<List<Address>>{
        try {
           val response = AddressDummy.listSavedAddress
           roomDatabase.addressDao().deleteAllAddress()
           response.forEach { address ->
               roomDatabase.addressDao().addAddress(address)
           }
        }catch (e: Exception){
            Log.d("TAG", "getSavedAddress: ${e.message}")
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
    //ORDER
    suspend fun addNewOrder(garbage: List<GarbageAdded>, user: User, address: Address, totalTransaction: Long, location: android.location.Location?){
        try {
            val id = generateUUID()
            val locationId = generateUUID()
            val order = Order(
                id = id,
                status = StatusOrderItem.NOT_TAKEN,
                totalTransaction = totalTransaction,
                userId = user.id,
                mitraId = "",
                locationId = locationId,
                addressId = address.id,
                created = "now"
            )
            roomDatabase.orderDao().addOrder(order)
            location?.let{
                roomDatabase.locationDao().addLocation(Location(id = locationId, latitude = location.latitude, longitude = location.longitude))
            }
            garbage.forEach { item -> roomDatabase.detailTransactionDao().addDetailTransaction(
                DetailTransaction(orderId = id, garbageId = item.garbage.id, qty = item.amount, total = item.totalPrice)
            ) }
        }catch (e: Exception){
            Log.d("TAG", "addNewOrder: $e")
        }
    }
    suspend fun updateOrderStatus(order: Order, statusOrderItem: StatusOrderItem): Flow<Boolean>{
        try {
            roomDatabase.orderDao().updateOrder(order.copy(status = statusOrderItem))
            return flowOf(true)
        }catch (e: Exception){
            throw e
        }
    }
    suspend fun getAvailableOrder(): Flow<List<OrderWithDetailTransaction>>{
        try {
            // TODO: fetch data from api
        }catch (e: Exception){
            throw e
        }
        return flowOf(roomDatabase.orderDao().getAvailableOrderWithTransactions())
    }
    // TODO: UPDATE ALL REPOSITORY METHOD WHEN API IS READY
    suspend fun getOrderDetail(orderId: String): Flow<OrderWithDetailTransaction> {
        try {
            //fetch api
            //delete all local data
            //insert to local
        }catch (e: Exception){
            throw e
        }

        return flowOf(roomDatabase.orderDao().getDetailOrder(orderId))
    }
    //Chat
    suspend fun getChatRooms(referenceTask: Task<List<String>>): Flow<List<String>>{
//        val reference = FireBaseRealtimeDatabase.getAllRoomsKey()
        var response: List<String> = listOf()
        referenceTask.addOnCompleteListener{
            if(it.isSuccessful){
                Log.d("UsersChat", "UsersChatsScreen: ${ it.result}")
                response = it.result
            }
            if(it.isCanceled){
                throw Exception(it.exception?.message)
            }
        }

        return flowOf(response)
    }

    companion object{
        val TAG = MainRepository::class.java.simpleName

        @Volatile
        private var INSTANCE: MainRepository? = null

        fun getInstance(datastore: DataStorePreferences, roomDatabase: MainDatabase, context: Context): MainRepository = INSTANCE ?: synchronized(this){
            MainRepository(datastore, roomDatabase, context).apply {
                INSTANCE = this
            }
        }
    }
}