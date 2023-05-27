package com.bangkit.ecoease.data.room.dao

import androidx.room.*
import com.bangkit.ecoease.data.room.model.DetailTransaction

@Dao
interface DetailTransactionDao {
    @Insert
    suspend fun addDetailTransaction(detailTransaction: DetailTransaction)

    @Delete
    suspend fun deleteDetailTransaction(detailTransaction: DetailTransaction)

    @Update
    suspend fun updateDetailTransaction(detailTransaction: DetailTransaction)

    @Query("DELETE FROM detail_transactions")
    suspend fun deleteAllDetailTransaction()
}