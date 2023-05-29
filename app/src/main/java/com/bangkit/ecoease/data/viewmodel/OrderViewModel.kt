package com.bangkit.ecoease.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.Order
import com.bangkit.ecoease.data.model.OrderHistory
import com.bangkit.ecoease.data.repository.MainRepository
import com.bangkit.ecoease.data.room.model.*
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: MainRepository): ViewModel() {
    private val garbage = MutableStateFlow<MutableList<GarbageAdded?>>(mutableListOf())
    private val _orderState = MutableStateFlow(Order(garbageList = listOf(), total = 0))
    private val _orderHistoryState = MutableStateFlow<UiState<List<OrderWithDetailTransaction>>>(UiState.Loading)
    private val _updateOrderStatusState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    private val _detailOrderState = MutableStateFlow<UiState<OrderWithDetailTransaction>>(UiState.Loading)
    private val _availableOrders = MutableStateFlow<UiState<List<OrderWithDetailTransaction>>>(UiState.Loading)

    val orderState: StateFlow<Order> = _orderState
    val orderHistoryState: StateFlow<UiState<List<OrderWithDetailTransaction>>> = _orderHistoryState
    val updateOrderStatusState: StateFlow<UiState<Boolean>> = _updateOrderStatusState
    val detailOrderState: StateFlow<UiState<OrderWithDetailTransaction>> = _detailOrderState
    val availableOrders: StateFlow<UiState<List<OrderWithDetailTransaction>>> = _availableOrders

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
    // TODO: FIX the  duplicate garbage order
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
    fun makeOrder(listGarbage: List<GarbageAdded>, totalTransaction: Long, location: android.location.Location?){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //get user data
                val user = repository.getUser()
                //get address
                val selectedAddress = repository.getSelectedAddress()
                //add new order
                selectedAddress.first()?.let { address ->

                    repository.addNewOrder(
                        listGarbage,
                        user.first(),
                        address,
                        totalTransaction,
                        location
                    )
                }
            }catch (e: Exception){
                Log.d("TAG", "makeOrder: ${e.message}")
            }
        }
    }
    fun loadDetailOrder(orderId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getOrderDetail(orderId).catch {
                    _detailOrderState.value = UiState.Error(it.message.toString())
                }.collect{data ->
                    _detailOrderState.value = UiState.Success(data)
                }
            }catch (e: Exception){
                _detailOrderState.value = UiState.Error(e.message.toString())
            }
        }
    }
    fun reloadDetailOrder(){
        _detailOrderState.value = UiState.Loading
    }
    fun updateOrder(order: com.bangkit.ecoease.data.room.model.Order, statusOrderItem: StatusOrderItem){
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.updateOrderStatus(order = order, statusOrderItem = statusOrderItem).catch {
                    _updateOrderStatusState.value = UiState.Error(it.message.toString())
                }.collect{
                    _updateOrderStatusState.value = UiState.Success(it)
                }
            }catch (e: Exception){
                Log.d("TAG", "updateOrder: ${e.message}")
                _updateOrderStatusState.value = UiState.Error(e.message.toString())
            }
        }
    }
    fun loadAvailableOrder(){
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repository.getAvailableOrder().catch {
                    _availableOrders.value = UiState.Error("error: ${it.message}")
                }.collect{
                    _availableOrders.value = UiState.Success(it)
                }
            }
        }catch (e: Exception){
            _availableOrders.value = UiState.Error("error: ${e.message}")
        }
    }
}