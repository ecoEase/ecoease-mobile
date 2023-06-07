package com.bangkit.ecoease.data.event

sealed class MyEvent {
    data class MessageEvent(val message: String): MyEvent()
}