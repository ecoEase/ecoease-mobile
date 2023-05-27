package com.bangkit.ecoease.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.viewmodel.*


class ViewModelFactory(private val repository: MainRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AddressViewModel::class.java) -> AddressViewModel(repository) as T
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository) as T
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> CameraViewModel(repository) as T
            modelClass.isAssignableFrom(GarbageViewModel::class.java) -> GarbageViewModel(repository) as T
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> OrderViewModel(repository) as T
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(repository) as T
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(repository) as T
            else -> throw java.lang.IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
        }
    }
}