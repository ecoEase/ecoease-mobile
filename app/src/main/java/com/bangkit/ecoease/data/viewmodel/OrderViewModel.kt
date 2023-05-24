package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.Order
import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.room.model.Address
import com.bangkit.ecoease.data.room.model.Garbage
import com.bangkit.ecoease.data.room.model.OrderWithGarbage
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: MainRepository): ViewModel() {
    private val garbage = MutableStateFlow<MutableList<GarbageAdded?>>(mutableListOf())
    private val _orderState = MutableStateFlow(Order(garbageList = listOf(), total = 0))
    private val _orderHistoryState = MutableStateFlow<UiState<List<OrderWithGarbage>>>(UiState.Loading)

    val orderState: StateFlow<Order> = _orderState
    val orderHistoryState: StateFlow<UiState<List<OrderWithGarbage>>> = _orderHistoryState

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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = repository.getUser().first().id
                repository.getAllOrderHistories(userId).catch {error ->
                    _orderHistoryState.value = UiState.Error("error: ${error.message}")
                }.collect{result ->
                    _orderHistoryState.value = UiState.Success(result)
                }
            }catch (e: Exception){
                    _orderHistoryState.value = UiState.Error("error: ${e.message}")
            }
        }
    }

    fun reloadOrderHistory(){
        _orderHistoryState.value = UiState.Loading
    }

    fun makeOrder(listGarbage: List<Garbage>, totalTransaction: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //get user data
                val user = repository.getUser()
//                Log.d(OrderViewModel::class.java.simpleName, "user: ${user.first()}")
//                Log.d(OrderViewModel::class.java.simpleName, "list garbage: $listGarbage")
//                Log.d(OrderViewModel::class.java.simpleName, "total: $totalTransaction")
                //get address
                val selectedAddress = repository.getSelectedAddress()
                //add new order
                selectedAddress.first()?.let {

                    repository.addNewOrder(
                        listGarbage,
                        user.first(),
                        it,
                        totalTransaction
                    )
                }
            }catch (e: Exception){
                Log.d("TAG", "makeOrder: ${e.message}")
            }
        }
    }
}