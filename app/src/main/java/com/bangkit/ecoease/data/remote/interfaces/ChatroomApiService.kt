package com.bangkit.ecoease.data.remote.interfaces

import com.bangkit.ecoease.data.model.request.Chatroom
import com.bangkit.ecoease.data.model.request.Login
import com.bangkit.ecoease.data.remote.responseModel.RegisterResponse
import com.bangkit.ecoease.data.remote.responseModel.UserLoginResponse
import com.bangkit.ecoease.data.remote.responseModel.chatroom.AddChatroomResponse
import com.bangkit.ecoease.data.remote.responseModel.chatroom.ChatroomResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ChatroomApiService{
    @GET("chatrooms")
    suspend fun getChatrooms(@Header("Authorization") token: String, @Query("userId") userId: String? = null, @Query("mitraId") mitraId: String? = null): ChatroomResponse

    @POST("chatrooms")
    suspend fun addChatroom(@Header("Authorization") token: String, @Body body: Chatroom): AddChatroomResponse

}