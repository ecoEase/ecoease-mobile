package com.bangkit.ecoease.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bangkit.ecoease.data.room.dao.Address
import com.bangkit.ecoease.data.room.dao.AddressDao

@Database(entities = [(Address::class)], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase(){
    abstract fun addressDao(): AddressDao

    companion object{
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase{
            if(INSTANCE == null) synchronized(MainDatabase::class.java){
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    MainDatabase::class.java,
                    "ecoease_database"
                ).fallbackToDestructiveMigration().build()
            }

            return INSTANCE as MainDatabase
        }
    }
}