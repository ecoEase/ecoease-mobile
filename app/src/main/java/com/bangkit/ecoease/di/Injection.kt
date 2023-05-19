package com.bangkit.ecoease.di

import android.content.Context
import com.bangkit.ecoease.data.datastore.DataStorePreferences
import com.bangkit.ecoease.data.repository.MainRepository

object Injection {
    fun provideInjection(context: Context): MainRepository{
        val datastore = DataStorePreferences.getInstances(context)
        return MainRepository.getInstance(datastore)
    }
}