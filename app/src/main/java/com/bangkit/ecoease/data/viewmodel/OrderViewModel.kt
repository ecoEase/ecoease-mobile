package com.bangkit.ecoease.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList

class OrderViewModel: ViewModel() {
    private val garbages = MutableStateFlow<MutableList<GarbageAdded?>>(mutableListOf())
    private val _orderState = MutableStateFlow<Order>(Order(garbages = listOf(), total = 0))
    val orderState: StateFlow<Order> = _orderState

    private fun resetCurrentOrder(){
        _orderState.value = Order(garbages = listOf(), total = 0)
    }

    private fun calculateCurrentOrder(){
        val currentTotal = garbages.value.map { garbage ->
            garbage?.let {
                it.garbage.price * it.amount
            } ?: 0
        }.reduce { acc, i -> acc + i }
        _orderState.value = Order(garbages = garbages.value, total = currentTotal)
    }

    fun addGarbageSlot(){
        garbages.value.add(null)
        calculateCurrentOrder()
    }

    fun deleteGarbageAt(index: Int){
        if(garbages.value.size <= 1){
            garbages.value = mutableListOf()
            resetCurrentOrder()
        } else {
            garbages.value.removeAt(index)
            calculateCurrentOrder()
        }
    }

    fun updateGarbage(index: Int, garbage: GarbageAdded){
        garbages.value[index] = garbage
        calculateCurrentOrder()
    }
}