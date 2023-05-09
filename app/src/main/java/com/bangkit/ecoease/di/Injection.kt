package com.bangkit.ecoease.di

import android.content.Context
import com.bangkit.ecoease.data.repository.MainRepository

object Injection {
    fun provideInjection(context: Context): MainRepository{
        return MainRepository.getInstance()
    }
}