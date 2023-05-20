package com.bangkit.ecoease.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.Order
import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: MainRepository): ViewModel() {
    private val garbage = MutableStateFlow<MutableList<GarbageAdded?>>(mutableListOf())
    private val _orderState = MutableStateFlow(Order(garbageList = listOf(), total = 0))
    private val _orderHistoryState = MutableStateFlow<UiState<List<OrderHistory>>>(UiState.Loading)

    val orderState: StateFlow<Order> = _orderState
    val orderHistoryState: StateFlow<UiState<List<OrderHistory>>> = _orderHistoryState

    private fun calculateCurrentOrder(){
        val currentTotal = garbage.value.map { garbage ->
            garbage?.let {
                it.garbage.price * it.amount
            } ?: 0
        }.reduce { acc, i -> acc + i }
        _orderState.value = Order(garbageList = garbage.value, total = currentTotal)
    }
    fun resetCurrentOrder(){
        garbage.value = mutableListOf()
        _orderState.value = Order(garbageList = listOf(), total = 0)
    }
    fun addGarbageSlot(){
        garbage.value.add(null)
        calculateCurrentOrder()
    }
    fun deleteGarbageAt(index: Int){
        if(garbage.value.size <= 1){
            garbage.value = mutableListOf()
            resetCurrentOrder()
        } else {
            garbage.value.removeAt(index)
            calculateCurrentOrder()
        }
    }
    fun updateGarbage(index: Int, garbage: GarbageAdded){
        this.garbage.value[index] = garbage
        calculateCurrentOrder()
    }
    //HISTORY
    fun loadOrderHistory(){
        viewModelScope.launch {
            try {
                repository.getAllOrderHistories().catch {error ->
                    _orderHistoryState.value = UiState.Error("error: ${error.message}")
                }.collect{result ->
                    _orderHistoryState.value = UiState.Success(result)
                }
            }catch (e: Exception){
                    _orderHistoryState.value = UiState.Error("error: ${e.message}")
            }
        }
    }
}