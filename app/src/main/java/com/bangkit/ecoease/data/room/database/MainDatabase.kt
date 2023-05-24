package com.bangkit.ecoease.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bangkit.ecoease.data.room.dao.*
import com.bangkit.ecoease.data.room.model.*

@Database(
    entities =
    [
        (Address::class),
        (ChatRoom::class),
        (Message::class),
        (Garbage::class),
        (User::class),
        (Order::class),
        (CrossOrderGarbage::class)
   ],
    version = 3,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase(){
    abstract fun addressDao(): AddressDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun messageDao(): MessageDao
    abstract fun garbageDao(): GarbageDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun crossOrderGarbageDao(): CrossOrderGarbageDao

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