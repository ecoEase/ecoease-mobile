package com.bangkit.ecoease.data.room.dao

import androidx.room.*
import com.bangkit.ecoease.data.room.model.Address

@Dao
interface AddressDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    @Update
    suspend fun updateAddress(address: Address)
    @Update
    suspend fun updateBatchAddresses(addresses: List<Address>)

    @Query("SELECT * FROM address")
    fun getAllAddress(): List<Address>

    @Query("SELECT * FROM address WHERE id = :id")
    fun getAddress(id: String): Address

    @Query("SELECT * FROM address WHERE selected = true LIMIT 1")
    fun getSelectedAddress(): Address?

    @Query("DELETE FROM address")
    suspend fun deleteAllAddress()
}