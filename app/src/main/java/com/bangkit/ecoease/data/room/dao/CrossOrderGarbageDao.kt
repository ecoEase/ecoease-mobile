package com.bangkit.ecoease.data.room.dao

import androidx.room.*
import com.bangkit.ecoease.data.room.model.CrossOrderGarbage

@Dao
interface CrossOrderGarbageDao {
    @Insert
    suspend fun addCrossOrderGarbage(orderGarbage: CrossOrderGarbage)

    @Delete
    suspend fun deleteCrossOrderGarbage(orderGarbage: CrossOrderGarbage)

    @Query("DELETE FROM CrossOrderGarbage")
    fun deleteAllCrossOrderGarbage()
}