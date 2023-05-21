package com.bangkit.ecoease.data.room.dao

import androidx.room.*
import com.bangkit.ecoease.data.room.model.ChatRoom

@Dao
interface ChatRoomDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addChatroom(chatRoom: ChatRoom)

    @Delete
    suspend fun deleteChatroom(chatRoom: ChatRoom)

    @Query("SELECT * FROM chat_room")
    fun getAllChatroom() : List<ChatRoom>

    @Query("SELECT * FROM chat_room WHERE user_id = :id")
    fun getChatroomByUserId(id: String) : List<ChatRoom>

    @Query("SELECT * FROM chat_room WHERE mitra_id = :id")
    fun getChatroomByMitraId(id: String) : List<ChatRoom>
}