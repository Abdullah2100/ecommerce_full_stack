package com.example.e_commerc_delivery_man.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerc_delivery_man.data.repository.OrderRepository
import com.example.e_commerc_delivery_man.model.DtoToModel.toOrder
import com.example.eccomerce_app.dto.response.OrderDto
import com.example.eccomerce_app.model.Order
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import android.util.Log
import com.example.e_commerc_delivery_man.data.repository.OrderItemRepository
import com.example.eccomerce_app.dto.response.OrderItemStatusChangeDto
import com.example.eccomerce_app.dto.response.OrderItemsStatusEvent
import com.example.eccomerce_app.dto.response.OrderUpdateEvent
import com.example.eccomerce_app.dto.response.OrderUpdateStatusDto
import com.example.eccomerce_app.dto.response.UpdateOrderStatus
import javax.inject.Named

class OrderViewModel(
    val orderRepository: OrderRepository,
    val orderItemRepository: OrderItemRepository,
    @Named("orderHub") val orderSocket: HubConnection?,
    @Named("orderItemHub") val orderItemSocket: HubConnection?
) : ViewModel() {

    private val _orderHub = MutableStateFlow<HubConnection?>(null)
    private val _orderItemHub = MutableStateFlow<HubConnection?>(null)


    private val _orders = MutableStateFlow<List<Order>?>(null);
    val orders = _orders.asStateFlow();

    private val _myOrders = MutableStateFlow<List<Order>?>(null);
    val myOrders = _myOrders.asStateFlow();


    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }

    init {
        initialFun()
    }

    fun initialFun() {
        getMyOrders(mutableStateOf(1))
        getOrders(mutableStateOf(1), null)
        if (orderSocket != null) {
            connection()
        }
    }

    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (_orderHub.value != null)
                _orderHub.value!!.stop()
        }
        super.onCleared()
    }

    fun connection() {
        if (orderSocket != null) {
            viewModelScope.launch(Dispatchers.IO + _coroutineException) {
//                try {
                _orderHub.emit(orderSocket)
                _orderItemHub.emit(orderItemSocket)
                _orderHub.value?.start()?.blockingAwait()
                _orderItemHub.value?.start()?.blockingAwait()
                _orderHub.value?.on(
                    "orderItemsStatusChange",
                    { response ->

                        val orderHolder =
                            _orders.value?.firstOrNull { it.id == response.orderId }
                        val myOrderHolder =
                            _myOrders.value?.firstOrNull { it.id == response.orderId }

                        if (orderHolder != null) {
                            val orderItemsHolder = orderHolder.orderItems.map { oi ->
                                if (oi.id == response.orderItemId) {
                                    oi.copy(orderItemStatus = response.status)
                                } else oi
                            }
                            val orderUpdated = _orders.value?.map {

                                if (it.id == response.orderId)
                                    it.copy(orderItems = orderItemsHolder)
                                else it
                            }
                            viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                                _orders.emit(orderUpdated)
                            }
                        } else if (myOrderHolder != null) {
                            val orderItemsHolder = myOrderHolder.orderItems.map { oi ->
                                if (oi.id == response.orderItemId) {
                                    oi.copy(orderItemStatus = response.status)
                                } else oi
                            }

                            val myOrderUpdated = _myOrders.value?.map {
                                if (it.id == response.orderId)
                                    it.copy(orderItems = orderItemsHolder)
                                else it
                            }
                            viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                                _myOrders.emit(myOrderUpdated)
                            }
                        }


                    },
                    OrderItemStatusChangeDto::class.java
                )
                _orderHub.value?.on(
                    "createdOrder",
                    { response ->
                        val orderListHolder = mutableListOf<Order>()
                        orderListHolder.add(response.toOrder())
                        if (!_orders.value.isNullOrEmpty()) {
                            orderListHolder.addAll(_orders.value!!)
                        }
                        viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
                            _orders.emit(orderListHolder);

                        }
                    },
                    OrderDto::class.java
                )
                _orderHub.value?.on(
                    "orderStatus",
                    { response ->

                        val orderUpdateData = _myOrders.value?.map { data ->
                            if (data.id == response.id) {
                                data.copy(status = response.status)
                            } else data
                        }


                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            _myOrders.emit(orderUpdateData)
                        }
                    },
                    OrderUpdateStatusDto::class.java
                )

                _orderItemHub.value?.on(
                    "orderGettingByDelivery",
                    { response ->
                        val orderWithOutCurrent =
                            _orders.value?.filter { o -> o.id != response.id }
                        val currentOrder =
                            _orders.value?.firstOrNull { o -> o.id == response.id };

                        val myOrderListHolder = mutableListOf<Order>()
                        if (currentOrder != null)
                            myOrderListHolder.add(currentOrder)

                        if (_myOrders.value != null)
                            myOrderListHolder.addAll(_myOrders.value!!)

                        viewModelScope.launch(Dispatchers.IO) {
                            if (orderWithOutCurrent != null)
                                _orders.emit(orderWithOutCurrent);
                            _myOrders.emit(myOrderListHolder)
                        }
                    },
                    OrderUpdateEvent::class.java
                )

                _orderItemHub.value?.on(
                    "orderItemsStatusChange",
                    { response ->
                        val orderHolder =
                            _myOrders.value?.map { data ->
                                if (data.id == response) {
                                    data.orderItems.map { oi ->
                                        if (oi.id == response.orderItemId)
                                            oi.copy(orderItemStatus = response.Status)
                                    }
                                } else data

                            } as List<Order>

                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            if (!orderHolder.isNullOrEmpty())
                                _myOrders.emit(orderHolder)
                        }
                    },
                    OrderItemsStatusEvent::class.java
                )
            }

        }
    }


    fun getOrders(
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.Default + _coroutineException) {
            if (isLoading != null)
                delay(500)
            val result = orderRepository.getOrdersNoSubmitted(pageNumber.value)
            when (result) {

                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<OrderDto>
                    val orderList = mutableListOf<Order>()
                    orderList.addAll(data.map { it.toOrder() })
                    if (!_orders.value.isNullOrEmpty()) {
                        orderList.addAll(_orders.value!!)
                    }
                    val distinctOrder = orderList.distinctBy { it.id }.toList()
                    _orders.emit(distinctOrder)

                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++
                }

                is NetworkCallHandler.Error -> {
                    if (_orders.value == null) {
                        _orders.emit(emptyList())
                    }
                    val errorMessage = result.data as String
                    Log.d("errorFromGettingOrder", errorMessage)
                    if (isLoading != null) isLoading.value = false

                }
            }

        }
    }


    fun getMyOrders(
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.Default + _coroutineException) {
            if (isLoading != null)
                delay(500)
            val result = orderRepository.getOrdersBelongToMe(pageNumber.value)
            when (result) {

                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<OrderDto>
                    val orderList = mutableListOf<Order>()
                    orderList.addAll(data.map { it.toOrder() })

                    if (!_myOrders.value.isNullOrEmpty()) {
                        orderList.addAll(_myOrders.value!!)
                    }

                    val distinctOrder = orderList.distinctBy { it.id }.toList()

                    _myOrders.emit(distinctOrder)

                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++
                }

                is NetworkCallHandler.Error -> {
                    if (_myOrders.value == null) {
                        _myOrders.emit(emptyList())
                    }
                    val errorMessage = result.data as String
                    Log.d("errorFromGettingOrder", errorMessage)
                    if (isLoading != null) isLoading.value = false

                }
            }

        }
    }


    suspend fun takeOrder(
        orderId: UUID,
    ): String? {
        val result = orderRepository.takeOrder(orderId)
        when (result) {

            is NetworkCallHandler.Successful<*> -> {
                val newOrder = _orders.value?.filter { x -> x.id != orderId };
                _orders.emit(newOrder)
                return null
            }

            is NetworkCallHandler.Error -> {
                if (_orders.value == null) {
                    _orders.emit(emptyList())
                }
                val errorMessage = result.data as String
                Log.d("errorFromGettingOrder", errorMessage)

                return result.data
            }
        }


    }


    suspend fun cancelOrder(
        orderId: UUID,
    ): String? {
        val result = orderRepository.cancelOrder(orderId)
        when (result) {

            is NetworkCallHandler.Successful<*> -> {
                val newOrder = _myOrders.value?.filter { x -> x.id != orderId };
                _myOrders.emit(newOrder)
                return null
            }

            is NetworkCallHandler.Error -> {
                if (_orders.value == null) {
                    _orders.emit(emptyList())
                }
                val errorMessage = result.data as String
                Log.d("errorFromGettingOrder", errorMessage)

                return result.data
            }
        }


    }


    //this function to handle if delivery collect the order from store or giving the order to user
    suspend fun updateStatus(id: String): String? {

        val idUUID = UUID.fromString(id)
        val isInOrder = _myOrders.value?.firstOrNull { it.id == idUUID }

        when {
            isInOrder != null -> {
                val reqest =
                    orderRepository.updateOrderStatus(UpdateOrderStatus(Id = idUUID, Status = 5))
                return when (reqest) {
                    is NetworkCallHandler.Successful<*> -> {
                        null;
                    }

                    is NetworkCallHandler.Error -> {
                        reqest.data
                    }

                }
            }

            else -> {
                val reqest =
                    orderItemRepository.updateOrderItemStatus(
                        UpdateOrderStatus(
                            Id = idUUID,
                            Status = 2
                        )
                    )
                return when (reqest) {
                    is NetworkCallHandler.Successful<*> -> {
                        null;
                    }

                    is NetworkCallHandler.Error -> {
                        reqest.data
                    }

                }
            }
        }
        return null;

    }
}