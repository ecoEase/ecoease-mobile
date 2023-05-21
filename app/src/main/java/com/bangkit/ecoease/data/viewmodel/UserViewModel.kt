package com.bangkit.ecoease.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.room.model.User
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow

class UserViewModel(private val repository: MainRepository): ViewModel() {
    private var _user: MutableStateFlow<UiState<User>> = MutableStateFlow(UiState.Loading)
}