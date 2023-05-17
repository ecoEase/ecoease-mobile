package com.bangkit.ecoease.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.viewmodel.CameraViewModel
import com.bangkit.ecoease.data.viewmodel.OrderViewModel


class ViewModelFactory(private val repository: MainRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(CameraViewModel::class.java)){
            return CameraViewModel(repository) as T
        }
        if(modelClass.isAssignableFrom(OrderViewModel::class.java)){
            return OrderViewModel() as T
        }

        throw java.lang.IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}