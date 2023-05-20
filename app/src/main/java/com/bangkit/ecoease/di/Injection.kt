package com.bangkit.ecoease.di

import android.content.Context
import com.bangkit.ecoease.data.datastore.DataStorePreferences
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.room.database.MainDatabase

object Injection {
    fun provideInjection(context: Context): MainRepository{
        val datastore = DataStorePreferences.getInstances(context)
        val roomDatabase = MainDatabase.getInstance(context)
        return MainRepository.getInstance(datastore, roomDatabase)
    }
}