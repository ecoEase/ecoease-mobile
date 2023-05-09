package com.bangkit.ecoease.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecoease.data.viewmodel.CameraViewModel


class ViewModelFactory(): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(CameraViewModel::class.java)){
            return CameraViewModel() as T
        }

        throw java.lang.IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}