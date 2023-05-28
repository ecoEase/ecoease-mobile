package com.bangkit.ecoease.data.room.dao

import androidx.room.*
import com.bangkit.ecoease.data.room.model.Order
import com.bangkit.ecoease.data.room.model.OrderWithDetailTransaction
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
    fun getAllOrders() : List<OrderWithDetailTransaction>

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrdersWithTransaction() : List<OrderWithDetailTransaction>

    @Transaction
    @Query("SELECT * FROM orders WHERE user_id = :userId")
    fun getAllOrderFromUser(userId: String) : List<OrderWithDetailTransaction>

    @Transaction
    @Query("SELECT * FROM orders WHERE mitra_id = :mitraId")
    fun getAllOrderFromMitra(mitraId: String) : List<OrderWithDetailTransaction>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :id")
    fun getDetailOrder(id: String) : OrderWithDetailTransaction

//    @Transaction
//    @Query("SELECT * FROM orders WHERE id = :id")
//    fun getDetailOrderTransaction(id: String) : OrderWithDetailTransaction
}