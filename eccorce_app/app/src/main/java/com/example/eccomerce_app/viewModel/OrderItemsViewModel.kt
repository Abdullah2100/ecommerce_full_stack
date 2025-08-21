package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commercompose.model.DtoToModel.toOrderItem
import com.example.e_commercompose.model.OrderItem
import com.example.eccomerce_app.data.repository.OrderItemRepository
import com.example.eccomerce_app.dto.OrderDto
import com.example.eccomerce_app.dto.OrderItemDto
import com.example.eccomerce_app.dto.OrderItemsStatusEvent
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class OrderItemsViewModel(
    val orderItemRepository: OrderItemRepository,
    val webSocket: HubConnection?

) : ViewModel() {
    private val _hub = MutableStateFlow<HubConnection?>(null)

    private val _orderItemForMyStore = MutableStateFlow<List<OrderItem>?>(null)
    val orderItemForMyStore = _orderItemForMyStore.asStateFlow()

    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun connection() {

        if (webSocket != null) {
            viewModelScope.launch(Dispatchers.IO + _coroutineException) {

                _hub.emit(webSocket)
                _hub.value?.start()?.blockingAwait()

                _hub.value?.on(
                    "orderExceptedByAdmin",
                    { response ->
                        val orderItemList = mutableListOf<OrderItem>()


                        if (_orderItemForMyStore.value != null) {
                            orderItemList.addAll(_orderItemForMyStore.value!!)
                        }
                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            _orderItemForMyStore.emit(orderItemList.distinctBy { it.id }.toList())
                        }
                    },
                    OrderDto::class.java
                )
                _hub.value?.on(
                    "orderItemsStatusChange",
                    { response ->
                        val myStoreOrderItemHolder = _orderItemForMyStore.value?.map {
                            if (it.id == response.OrderId) {
                                it.copy(orderItemStatus = response.Status)

                            } else it
                        }


                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            _orderItemForMyStore.emit(myStoreOrderItemHolder)
                        }
                    },
                    OrderItemsStatusEvent::class.java
                )


            }

        }
    }


    init {
        connection()
    }

    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (_hub.value != null)
                _hub.value!!.stop()
        }
        super.onCleared()
    }

    fun getMyOrderItemBelongToMyStore(
        storeId: UUID,
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null
    ) {

        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (isLoading != null) {
                isLoading.value = true
                delay(500)
            }
            val result = orderItemRepository.getMyOrderItemForStoreId(storeId, pageNumber.value)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<OrderItemDto>
                    val orderItemList = mutableListOf<OrderItem>()
                    orderItemList.addAll(data.map { it.toOrderItem() })
                    if (!_orderItemForMyStore.value.isNullOrEmpty()) {
                        orderItemList.addAll(_orderItemForMyStore.value!!)
                    }
                    val distinctOrderItem = orderItemList.distinctBy { it.id }.toList()
                    _orderItemForMyStore.emit(distinctOrderItem)
                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25) pageNumber.value++

                }

                is NetworkCallHandler.Error -> {
                    if (_orderItemForMyStore.value == null) {
                        _orderItemForMyStore.emit(emptyList())
                    }
                    if (isLoading != null) isLoading.value = false

                    val errorMessage = result.data as String
                    Log.d("errorFromGettingOrder", errorMessage)
                    if (_orderItemForMyStore.value == null) {
                        _orderItemForMyStore.emit(emptyList())
                    }
                }
            }

        }
    }


    suspend fun updateOrderItemStatusFromStore(id: UUID, status: Int): String? {
        val result = orderItemRepository.updateOrderItemStatus(id, status)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val orderItemStatus = when (status) {
                    0 -> "Excepted"
                    else -> "Cancelled"
                }
                val updateOrderItem = _orderItemForMyStore.value?.map { it ->
                    if (it.id == id) {
                        it.copy(orderItemStatus = orderItemStatus)
                    } else {
                        it
                    }
                }
                _orderItemForMyStore.emit(updateOrderItem)
                return null
            }

            is NetworkCallHandler.Error -> {
                val errorMessage = result.data as String
                Log.d("errorFromGettingOrder", errorMessage)
                if (_orderItemForMyStore.value == null) {
                    _orderItemForMyStore.emit(emptyList())
                }
                return errorMessage
            }
        }

    }


}