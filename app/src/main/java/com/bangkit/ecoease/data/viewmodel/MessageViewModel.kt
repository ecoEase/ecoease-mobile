package com.bangkit.ecoease.data.viewmodel

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.event.MyEvent
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.room.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MessageViewModel(private val repository: MainRepository) : ViewModel() {
    private val eventChannel = Channel<MyEvent>()
    val eventFlow = eventChannel.receiveAsFlow()
    private var _user: Flow<User>? = null
    val user: Flow<User>? = _user

    init {
        getCurrent()
    }

    private fun getCurrent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                 _user = repository.getUser()
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }
}