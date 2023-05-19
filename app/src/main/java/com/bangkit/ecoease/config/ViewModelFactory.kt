package com.bangkit.ecoease.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.viewmodel.CameraViewModel
import com.bangkit.ecoease.data.viewmodel.OrderViewModel
import com.bangkit.ecoease.data.viewmodel.SplashViewModel


class ViewModelFactory(private val repository: MainRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> CameraViewModel(repository) as T
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> OrderViewModel() as T
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(repository) as T
            else -> throw java.lang.IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
        }
    }
}