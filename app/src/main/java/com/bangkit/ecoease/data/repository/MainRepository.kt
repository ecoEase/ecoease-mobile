package com.bangkit.ecoease.data.repository

import android.content.Context
import android.util.Log
import com.bangkit.ecoease.config.ApiConfig
import com.bangkit.ecoease.data.datastore.DataStorePreferences
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.model.request.DetailTransactionsItem
import com.bangkit.ecoease.data.model.request.Login
import com.bangkit.ecoease.data.model.request.OrderWithDetail
import com.bangkit.ecoease.data.model.request.Register
import com.bangkit.ecoease.data.remote.responseModel.toAddress
import com.bangkit.ecoease.data.remote.responseModel.toGarbage
import com.bangkit.ecoease.data.remote.responseModel.toUser
import com.bangkit.ecoease.data.room.database.MainDatabase
import com.bangkit.ecoease.data.room.model.*
import com.bangkit.ecoease.helper.toOrderWithDetailTransaction
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class MainRepository(
    private val datastore: DataStorePreferences,
    private val roomDatabase: MainDatabase,
    val context: Context
) {
    private var capturedImageUri: ImageCaptured? = null

    // TODO: add API service for each endpoint
    private val garbageApiService = ApiConfig.getGarbageApiService()
    private val userApiService = ApiConfig.getUserApiService()
    private val addressApiService = ApiConfig.getAddressApiService()
    private val orderApiService = ApiConfig.getOrderApiService()

    //CAMERA
    fun setCapturedImage(imageCapture: ImageCaptured) {
        capturedImageUri = imageCapture
    }

    fun getCapturedImage(): Flow<ImageCaptured> {
        Log.d(MainRepository::class.java.simpleName, "getCapturedImageUri: $capturedImageUri")
        return flowOf(capturedImageUri!!)
    }

    //ON BOARDING
    suspend fun getIsFinishOnboard(): Boolean = datastore.isFinishReadOnBoard().first()
    suspend fun finishOnBoard() {
        datastore.finishReadOnboard()
    }

    //AUTH
    suspend fun getToken(): String = datastore.getAuthToken().first()
    suspend fun setToken(newToken: String) {
        datastore.setToken(newToken)
    }

    //USER
    suspend fun registerUser(registerData: Register): Flow<Boolean> {
        try {
            // TODO: register bermasalah 
            Log.d(TAG, "registerUser: ${registerData.photoFile}")
            val response = userApiService.register(
                photoFile = registerData.photoFile,
                firstName = registerData.firstName,
                lastName = registerData.lastName,
                email = registerData.email,
                phoneNumber = registerData.phoneNumber,
                password = registerData.password,
            )
            if (response.data != null) return flowOf(true)
            throw Exception(response.message)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun loginUser(loginData: Login): Flow<String> {
        try {
            val response = userApiService.login(loginData)
            response.data?.let {
                val userData = it
                roomDatabase.userDao().deleteAll()
                roomDatabase.userDao().addUser(userData.toUser())
                setToken(response.token)
                return flowOf(response.message)
            }
            throw Exception(response.message)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun resetUser() {
        try {
            roomDatabase.userDao().deleteAll()
        } catch (e: Exception) {
            throw e
        }
    }

    fun getUser(): Flow<User> = flowOf(roomDatabase.userDao().getUser())

    //GARBAGE
    suspend fun getAllGarbage(): Flow<List<Garbage>> {
        try {
            val token = datastore.getAuthToken().first()
            val response = garbageApiService.get(token)
            if (response.data != null) {
                roomDatabase.garbageDao().deleteAllGarbage()
                response.data?.forEach { garbageItem ->
                    roomDatabase.garbageDao().addGarbage(garbageItem!!.toGarbage())
                }
            }
        } catch (e: Exception) {
            if (roomDatabase.garbageDao().getAllGarbage().isEmpty()) {
                Log.d(TAG, "getAllGarbage: $e")
                throw e
            }
        }
        return flowOf(roomDatabase.garbageDao().getAllGarbage())
    }

    //Address
    suspend fun getSavedAddress(): Flow<List<Address>> {
        try {
            val token = datastore.getAuthToken().first()
            val userId = roomDatabase.userDao().getUser().id
            val response = addressApiService.getSavedAddress(token = token, userId = userId)
            roomDatabase.addressDao().deleteAllAddress()

            if (response.data == null) throw Exception("data address is null")

            response.data.forEach { addressItem ->
                roomDatabase.addressDao().addAddress(addressItem.toAddress())
            }
        } catch (e: Exception) {
            Log.d("TAG", "getSavedAddress: ${e.message}")
            if (roomDatabase.addressDao().getAllAddress().isEmpty()) {
                throw e
            }
        }
        return flowOf(roomDatabase.addressDao().getAllAddress())
    }
    suspend fun addAddress(address: Address) {
        try {
            val token = datastore.getAuthToken().first()
            val userId = roomDatabase.userDao().getUser().id

            addressApiService.addNewAddress(token, com.bangkit.ecoease.data.model.request.Address(
                name = address.name,
                city = address.city,
                district = address.district,
                detail = address.detail,
                user_id = userId,
            ))

            roomDatabase.addressDao().addAddress(address)
        }catch (e: Exception){
            throw e
        }
    }
    suspend fun deleteAddress(address: Address) {
        try {
            val token = datastore.getAuthToken().first()
            addressApiService.deleteAddress(token, address.id)
//            roomDatabase.addressDao().deleteAddress(address)
        }catch (e: Exception){
            throw e
        }
    }
    suspend fun getSelectedAddress(): Flow<Address?> {
        var response: Flow<Address?>
        try {
            response = flowOf(roomDatabase.addressDao().getSelectedAddress())
        } catch (e: Exception) {
            throw e
        }
        return response
    }
    suspend fun saveSelectedAddress(address: Address) {
        try {
            val token = datastore.getAuthToken().first()
            val response = addressApiService.selectUseAddress(token, address.id)
            if (response.data == null) throw Exception("data address is null")

            response.data.forEach { addressItem ->
                roomDatabase.addressDao().addAddress(addressItem.toAddress())
            }
        }catch (e: Exception){
            throw e
        }
    }

    //ORDER HISTORY
    suspend fun getAllOrderHistories(userId: String): Flow<List<OrderWithDetailTransaction>> {
        var orderWithDetailTransaction: List<OrderWithDetailTransaction> = listOf()

        try {
            val token = datastore.getAuthToken().first()
            val userId = roomDatabase.userDao().getUser().id
            val response = orderApiService.getByUser(token, userId)

            response.data?.let{
                orderWithDetailTransaction = it.map { orderData -> orderData.toOrderWithDetailTransaction()}
            }
            if(response.data == null) throw Exception(response.message)
        }catch (e: Exception){
            throw e
        }
//        orderWithDetailTransaction = roomDatabase.orderDao().getAllOrderFromUser(userId)
//        val gson = Gson()
//        val json = gson.toJsonTree(orderWithDetailTransaction)

        return flowOf(orderWithDetailTransaction)
    }

    //ORDER
    suspend fun addNewOrder(
        garbage: List<GarbageAdded>,
        user: User,
        address: Address,
        totalTransaction: Long,
        location: android.location.Location?,
        mitra: Mitra? = null,
        date: String = "now",
    ) {
        try {
            val token = datastore.getAuthToken().first()
            val userId = roomDatabase.userDao().getUser().id

            val order = com.bangkit.ecoease.data.model.request.Order(
                status = StatusOrderItem.NOT_TAKEN.toString(),
                total_transaction = totalTransaction.toInt(),
                user_id = userId,
                address_id = address.id,
            )
            val listDetailTransactions = garbage.map {
                DetailTransactionsItem(
                    garbage_id = it.garbage.id,
                    total = it.totalPrice.toInt(),
                    qty = it.amount,
                )
            }
            val convertedLocation = if(location != null) com.bangkit.ecoease.data.model.request.Location(
                location.latitude,
                location.longitude
            ) else null
            val newOrderData = OrderWithDetail(
                order = order,
                detailTransactions = listDetailTransactions,
                location = convertedLocation
            )
            Log.d(TAG, "addNewOrder: $newOrderData")
            val response = orderApiService.addNewOrder(token, newOrderData)

            if(response.data == null) throw Exception(response.message)
        } catch (e: Exception) {
            Log.d("TAG", "addNewOrder: $e")
        }
    }

    suspend fun updateOrderStatus(order: Order, statusOrderItem: StatusOrderItem): Flow<Boolean> {
        try {
            roomDatabase.orderDao().updateOrder(order.copy(status = statusOrderItem))
            return flowOf(true)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getAvailableOrder(): Flow<List<OrderWithDetailTransaction>> {
        try {
            // TODO: fetch data from api
        } catch (e: Exception) {
            throw e
        }
        return flowOf(roomDatabase.orderDao().getAvailableOrderWithTransactions())
    }

    // TODO: UPDATE ALL REPOSITORY METHOD WHEN API IS READY
    suspend fun getOrderDetail(orderId: String): Flow<OrderWithDetailTransaction> {
        try {
            //fetch api
            val token = datastore.getAuthToken().first()
            val response = orderApiService.getById(token, orderId)
            if(response.data == null) throw Exception(response.message)

            return flowOf(response.data.toOrderWithDetailTransaction())
            //delete all local data
            //insert to local
        } catch (e: Exception) {
            throw e
        }
//        return flowOf(roomDatabase.orderDao().getDetailOrder(orderId))
    }


    //Chat
    suspend fun getChatRooms(referenceTask: Task<List<String>>): Flow<List<String>> {
//        val reference = FireBaseRealtimeDatabase.getAllRoomsKey()
        var response: List<String> = listOf()
        referenceTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("UsersChat", "UsersChatsScreen: ${it.result}")
                response = it.result
            }
            if (it.isCanceled) {
                throw Exception(it.exception?.message)
            }
        }

        return flowOf(response)
    }

    companion object {
        val TAG = MainRepository::class.java.simpleName

        @Volatile
        private var INSTANCE: MainRepository? = null

        fun getInstance(
            datastore: DataStorePreferences,
            roomDatabase: MainDatabase,
            context: Context
        ): MainRepository = INSTANCE ?: synchronized(this) {
            MainRepository(datastore, roomDatabase, context).apply {
                INSTANCE = this
            }
        }
    }
}