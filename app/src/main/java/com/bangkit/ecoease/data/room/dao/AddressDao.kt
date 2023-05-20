package com.bangkit.ecoease.data.room.dao

import androidx.room.*

@Dao
interface AddressDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    @Update
    suspend fun updateAddress(address: Address)

    @Query("SELECT * FROM address")
    fun getAllAddress(): List<Address>

    @Query("SELECT * FROM address WHERE id = :id")
    fun getAddress(id: String): Address
}