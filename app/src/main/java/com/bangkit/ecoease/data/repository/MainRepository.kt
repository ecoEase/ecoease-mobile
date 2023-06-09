package com.bangkit.ecoease.data.repository

import android.content.Context
import android.util.Log
import com.bangkit.ecoease.BuildConfig
import com.bangkit.ecoease.config.ApiConfig
import com.bangkit.ecoease.data.datastore.DataStorePreferences
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.model.request.*
import com.bangkit.ecoease.data.remote.responseModel.UserData
import com.bangkit.ecoease.data.remote.responseModel.address.toAddress
import com.bangkit.ecoease.data.remote.responseModel.chatroom.AddChatroomResponse
import com.bangkit.ecoease.data.remote.responseModel.chatroom.ChatRoomItem
import com.bangkit.ecoease.data.remote.responseModel.chatroom.DeleteChatroomResponse
import com.bangkit.ecoease.data.remote.responseModel.toGarbage
import com.bangkit.ecoease.data.remote.responseModel.toUser
import com.bangkit.ecoease.data.room.database.MainDatabase
import com.bangkit.ecoease.data.room.model.*
import com.bangkit.ecoease.data.room.model.Address
import com.bangkit.ecoease.data.room.model.Order
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
    private val chatRoomApiService = ApiConfig.getChatroomApiService()
    private val fcmServerApiService = ApiConfig.getFCMServerApiService()
    private val fcmClientApiService = ApiConfig.getFCMClientApiService()


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
            val response = userApiService.register(
                photoFile = registerData.photoFile,
                firstName = registerData.firstName,
                lastName = registerData.lastName,
                email = registerData.email,
                phone_number = registerData.phone_number,
                password = registerData.password,
            )
            if (response.data != null) return flowOf(true)
//            val response = dicodingApiService.register(
//                registerData.firstName,
//                registerData.photoFile
//            )
//            if(response.error == false) return flowOf(true)
            throw Exception(response.message)
        } catch (e: Exception) {
            Log.d("TAG", "registerUser error: ${e.message} ")
            throw e
        }
    }

    suspend fun loginUser(loginData: Login): Flow<UserData> {
        try {
            val response = userApiService.login(loginData)
            response.data?.let {
                val userData = it
                roomDatabase.userDao().deleteAll()
                roomDatabase.userDao().addUser(userData.toUser())
                setToken(response.token)
                return flowOf(it)
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

    suspend fun logout() {
        try {
            setToken("")
            roomDatabase.userDao().deleteAll()
            roomDatabase.mitraDao().deleteAll()
            roomDatabase.addressDao().deleteAllAddress()
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
            val response = addressApiService.getAll(token = token, userId = userId)
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

            addressApiService.addNewAddress(
                token, com.bangkit.ecoease.data.model.request.Address(
                    name = address.name,
                    city = address.city,
                    district = address.district,
                    detail = address.detail,
                    user_id = userId,
                )
            )

            roomDatabase.addressDao().addAddress(address)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteAddress(address: Address) {
        try {
            val token = datastore.getAuthToken().first()
            addressApiService.deleteAddress(token, address.id)
//            roomDatabase.addressDao().deleteAddress(address)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getSelectedAddress(): Flow<Address?> {
        try {
            val token = datastore.getAuthToken().first()
            val userId = roomDatabase.userDao().getUser().id
            val response = addressApiService.getAll(token = token, userId = userId)

            if(response.data == null) throw Exception(response.message)

            roomDatabase.addressDao().deleteAllAddress()
            response.data.forEach { address ->
                roomDatabase.addressDao().addAddress(address.toAddress() )
            }
            return flowOf(roomDatabase.addressDao().getSelectedAddress())
        } catch (e: Exception) {
            if(roomDatabase.addressDao().getSelectedAddress() != null) return flowOf(roomDatabase.addressDao().getSelectedAddress())
            throw e
        }
    }

    suspend fun saveSelectedAddress(address: Address) {
        try {
            val token = datastore.getAuthToken().first()
            val response = addressApiService.selectUseAddress(token, address.id)
            if (response.data == null) throw Exception("data address is null")

            response.data.forEach { addressItem ->
                roomDatabase.addressDao().addAddress(addressItem.toAddress())
            }
        } catch (e: Exception) {
            throw e
        }
    }

    //ORDER HISTORY
    suspend fun getAllOrderHistories(userId: String): Flow<List<OrderWithDetailTransaction>> {
        var orderWithDetailTransaction: List<OrderWithDetailTransaction> = listOf()

        try {
            val token = datastore.getAuthToken().first()
            val response = orderApiService.getByUser(token, userId)

            response.data?.let {
                orderWithDetailTransaction =
                    it.map { orderData ->
                        Log.d(TAG, "getAllOrderHistories: $orderData")
                        orderData.toOrderWithDetailTransaction()
                    }
            }
            if (response.data == null) throw Exception(response.message)
        } catch (e: Exception) {
            throw e
        }
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
            val convertedLocation =
                if (location != null) com.bangkit.ecoease.data.model.request.Location(
                    location.latitude,
                    location.longitude
                ) else null
            val newOrderData = OrderWithDetail(
                order = order,
                detailTransactions = listDetailTransactions,
                location = convertedLocation
            )
            val response = orderApiService.addNewOrder(token, newOrderData)

            if (response.data == null) throw Exception(response.message)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateOrderStatus(order: Order, statusOrderItem: StatusOrderItem) {
        try {
            val token = datastore.getAuthToken().first()
            val response = orderApiService.cancelOrder(
                token, UpdateOrder(
                    id = order.id,
                    status = statusOrderItem
                )
            )

            if (response.data == null) throw Exception(response.message)

            roomDatabase.orderDao().updateOrder(order.copy(status = statusOrderItem))
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getAvailableOrder(): Flow<List<OrderWithDetailTransaction>> {
        try {
            val token = datastore.getAuthToken().first()
            val response = orderApiService.getAvailable(token)
            if (response.data == null) throw Exception(response.message)
            return flowOf(response.data.map { it.toOrderWithDetailTransaction() })
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getOrderDetail(orderId: String): Flow<OrderWithDetailTransaction> {
        try {
            val token = datastore.getAuthToken().first()
            val response = orderApiService.getById(token, orderId)
            if (response.data == null) throw Exception(response.message)
            return flowOf(response.data.toOrderWithDetailTransaction())
        } catch (e: Exception) {
            throw e
        }
    }

    //Chat
    // TODO: change the functionality so it can create chatroom based userid and mitraid
    suspend fun createChatroom(body: Chatroom? = null): Flow<AddChatroomResponse>{
        try {
            val tokenAuth = datastore.getAuthToken().first()
            val userId = roomDatabase.userDao().getUser().id
            val response = chatRoomApiService.addChatroom(token = tokenAuth, body = Chatroom(
                user_id = userId,
                mitra_id = "815d6dbe-03d5-4642-b9bb-5e9defc7ff24"
            ))
            if(response.data == null) throw Exception(response.message)
            return flowOf(response)
        }catch (e: Exception){
            throw e
        }
    }

    suspend fun deleteChatroom(roomId: String): Flow<DeleteChatroomResponse>{
        try {
            val tokenAuth = datastore.getAuthToken().first()
            val response = chatRoomApiService.deleteChatroom(token = tokenAuth, id = roomId)
            if(response.data == null) throw Exception(response.message)
            return flowOf(response)
        }catch (e: Exception){
            throw e
        }
    }

    suspend fun getChatRooms(): Flow<List<ChatRoomItem>> {
        try {
            val tokenAuth = datastore.getAuthToken().first()
            val userId = roomDatabase.userDao().getUser().id
            val response = chatRoomApiService.getChatrooms(token = tokenAuth, userId = userId)
            if(response.data == null) throw Exception(response.message)
            return flowOf(response.data)
        }catch (e: Exception){
            throw e
        }
    }

    //FCM to handle notification
    suspend fun setFCMToken(id: String? = null, token: String) {
        try {
            val tokenAuth = datastore.getAuthToken().first()
            id?.let {
                fcmServerApiService.updateUserToken(
                    token = tokenAuth,
                    id = it,
                    body = UpdateFCMToken(token)
                )
            }
            datastore.setFCMToken(token)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getFCMToken(): String = datastore.getFCMToken().first()
    suspend fun sendNotification(body: FCMNotification) {
        try {
            val token = BuildConfig.FCM_key
            fcmClientApiService.sendNotification(token = token, body = body)
        } catch (e: Exception) {
            throw e
        }
    }

    companion object {
        private val TAG = MainRepository::class.java.simpleName

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