package com.bangkit.ecoease.data.room.dao

import androidx.room.*
import com.bangkit.ecoease.data.room.model.Order
import com.bangkit.ecoease.data.room.model.OrderWithGarbage

@Dao
interface OrderDao {
    @Insert
    suspend fun addOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrders() : List<OrderWithGarbage>

    @Transaction
    @Query("SELECT * FROM orders WHERE user_id = :userId")
    fun getAllOrderFromUser(userId: String) : List<OrderWithGarbage>

    @Transaction
    @Query("SELECT * FROM orders WHERE mitra_id = :mitraId")
    fun getAllOrderFromMitra(mitraId: String) : List<OrderWithGarbage>
}