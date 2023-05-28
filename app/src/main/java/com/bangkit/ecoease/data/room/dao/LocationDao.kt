package com.bangkit.ecoease.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bangkit.ecoease.data.room.model.Location

@Dao
interface LocationDao {
    @Insert
    suspend fun addLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("SELECT * FROM locations")
    fun getAllLocation() : List<Location>
}