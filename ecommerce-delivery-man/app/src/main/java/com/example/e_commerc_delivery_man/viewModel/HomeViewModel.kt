package com.example.e_commerc_delivery_man.viewModel

import android.Manifest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerc_delivery_man.Dto.OrderUpdateEvent
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.data.Room.AuthDao
import com.example.e_commerc_delivery_man.data.repository.HomeRepository
import com.example.e_commerc_delivery_man.dto.response.DeliveryInfoDto
import com.example.e_commerc_delivery_man.dto.response.OrderItemStatusChangeDto
import com.example.e_commerc_delivery_man.model.DeliveryInfo
import com.example.e_commerc_delivery_man.model.DtoToModel.toDeliveryInfo
import com.example.e_commerc_delivery_man.model.DtoToModel.toOrder
import com.example.e_commerc_delivery_man.model.DtoToModel.toVarient
import com.example.e_commercompose.dto.response.VarientResponseDto
import com.example.e_commercompose.model.VarientModel
import com.example.eccomerce_app.dto.response.OrderResponseDto
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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class HomeViewModel(
    val homeRepository: HomeRepository,
    val dao: AuthDao,
    var webSocket: HubConnection?,
    var context:Context
) : ViewModel() {

    private val _hub = MutableStateFlow<HubConnection?>(null)

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()


    private var _myInfo = MutableStateFlow<DeliveryInfo?>(null)
    var myInfo = _myInfo.asStateFlow()


    private var _orders = MutableStateFlow<List<Order>?>(null);
    var orders = _orders.asStateFlow();

    private var _myOrders = MutableStateFlow<List<Order>?>(null);
    var myOrders = _myOrders.asStateFlow();

    private var _varients = MutableStateFlow<MutableList<VarientModel>?>(null)
    var varients = _varients.asStateFlow()

    private var _coroutinExption = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }

    init {
        initialFun()
    }

    fun initialFun() {
        getMyInfo()
        getMyOrders(mutableStateOf(1))
        getOrders(mutableStateOf(1), null)
        if (webSocket != null) {
            connection()
        }
    }

    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO + _coroutinExption) {
            if (_hub.value != null)
                _hub.value!!.stop()
        }
        super.onCleared()
    }

    fun connection() {
        if (webSocket != null) {
            viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {

                _hub.emit(webSocket)
                _hub.value?.start()?.blockingAwait()
                _hub.value?.on(
                    "orderItemsStatusChange",
                    { response ->

                        var orderHolder = _orders.value?.firstOrNull { it.id == response.orderId }
                        var myOrderHolder =
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
                            viewModelScope.launch(Dispatchers.IO + _coroutinExption) {
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
                            viewModelScope.launch(Dispatchers.IO + _coroutinExption) {
                                _myOrders.emit(myOrderUpdated)
                            }
                        }


                    },
                    OrderItemStatusChangeDto::class.java
                )
                _hub.value?.on(
                    "createdOrder",
                    { response ->
                        val orderListHolder = mutableListOf<Order>()
                        orderListHolder.add(response.toOrder())
                        if (!_orders.value.isNullOrEmpty()) {
                            orderListHolder.addAll(_orders.value!!)
                        }
                        viewModelScope.launch(Dispatchers.IO+SupervisorJob()) {
                            _orders.emit(orderListHolder);
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {

                                showNotification("there are some orders submitted")
                            }
                        }
                    },
                    OrderResponseDto::class.java
                )

                _hub.value?.on(
                    "orderGettingByDelivery",
                    { response ->
                        val orderWithOutCurrent = _orders.value?.filter { o -> o.id != response.id }
                        val currentOrder = _orders.value?.firstOrNull { o -> o.id == response.id };

                        val myOrderListHolder = mutableListOf<Order>()
                        if (currentOrder != null)
                            myOrderListHolder.add(currentOrder)

                        if(_myOrders.value!=null)
                            myOrderListHolder.addAll(_myOrders.value!!)

                        viewModelScope.launch(Dispatchers.IO) {
                            if (orderWithOutCurrent != null)
                                _orders.emit(orderWithOutCurrent);
                            _myOrders.emit(myOrderListHolder)
                        }
                    },
                    OrderUpdateEvent::class.java
                )
            }

        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String?) {
        val channelId = "default_channel"
        val channelName = "Default Channel"
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(0, notification)
    }



    //delivery
    fun getMyInfo(isUpdated: Boolean = false) {
        if (_myInfo.value != null && isUpdated == false) return;
        viewModelScope.launch(Dispatchers.Default + _coroutinExption) {
            var result = homeRepository.getMyInfo();
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as DeliveryInfoDto
                    _myInfo.emit(data.toDeliveryInfo())

                }

                is NetworkCallHandler.Error -> {
                    if (_myInfo.value == null) {
                        _myInfo.emit(
                            null
                        )
                    }
                    var resultError = result.data as String
                    Log.d("errorFromNetowrk", resultError)
                }

                else -> {

                }
            }
        }
    }

    suspend fun updateMyInfo(
        status: Boolean
    ): String? {
        var result = homeRepository.updateDeliveryState(
            status
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as Boolean
                val updatedInfo = _myInfo.value?.copy(isAvaliable = status)
                _myInfo.emit(updatedInfo)
                return null;
            }

            is NetworkCallHandler.Error -> {

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "").toString()

            }

        }
    }

    /*    suspend fun updateMyInfo(
            userData: MyInfoUpdate

        ): String? {
            var result = homeRepository.UpdateMyInfo(
                userData
            );
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as UserDto
                    _myInfo.emit(data.toUser())
                    return null;
                }

                is NetworkCallHandler.Error -> {

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    return errorMessage.replace("\"", "").toString()

                }

            }
        }
    */


//orders


    fun getOrders(
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.Default + _coroutinExption) {
            if (isLoading != null)
                delay(500)
            var result = homeRepository.getOrders(pageNumber.value)
            when (result) {

                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<OrderResponseDto>
                    var orderList = mutableListOf<Order>()
                    orderList.addAll(data.map { it.toOrder() })
                    if (!_orders.value.isNullOrEmpty()) {
                        orderList.addAll(_orders.value!!)
                    }
                    val distincetOrder = orderList.distinctBy { it.id }.toList()
                    _orders.emit(distincetOrder)

                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++
                }

                is NetworkCallHandler.Error -> {
                    if (_orders.value == null) {
                        _orders.emit(emptyList())
                    }
                    var errorMessage = result.data as String
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
        if (_myOrders.value != null && pageNumber.value == 1) return;
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.Default + _coroutinExption) {
            if (isLoading != null)
                delay(500)
            var result = homeRepository.getOrdersBelongToMe(pageNumber.value)
            when (result) {

                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<OrderResponseDto>
                    var orderList = mutableListOf<Order>()
                    orderList.addAll(data.map { it.toOrder() })
                    if (!_myOrders.value.isNullOrEmpty()) {
                        orderList.addAll(_orders.value!!)
                    }
                    val distincetOrder = orderList.distinctBy { it.id }.toList()
                    _myOrders.emit(distincetOrder)

                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++
                }

                is NetworkCallHandler.Error -> {
                    if (_myOrders.value == null) {
                        _myOrders.emit(emptyList())
                    }
                    var errorMessage = result.data as String
                    Log.d("errorFromGettingOrder", errorMessage)
                    if (isLoading != null) isLoading.value = false

                }
            }

        }
    }


    suspend fun takeOrder(
        orderId: UUID,
    ): String? {
        var result = homeRepository.takeOrder(orderId)
        when (result) {

            is NetworkCallHandler.Successful<*> -> {

                return null
            }

            is NetworkCallHandler.Error -> {
                if (_orders.value == null) {
                    _orders.emit(emptyList())
                }
                var errorMessage = result.data as String
                Log.d("errorFromGettingOrder", errorMessage)

                return result.data
            }
        }


    }


    suspend fun cencleOrder(
        orderId: UUID,
    ): String? {
        var result = homeRepository.cencleOrder(orderId)
        when (result) {

            is NetworkCallHandler.Successful<*> -> {
                var ordersWithRemovedCurrent = _myOrders.value?.filter { it -> it.id != orderId }
                if (ordersWithRemovedCurrent != null) {

                    _myOrders.emit(ordersWithRemovedCurrent)
                }
                return null
            }

            is NetworkCallHandler.Error -> {
                if (_orders.value == null) {
                    _orders.emit(emptyList())
                }
                var errorMessage = result.data as String
                Log.d("errorFromGettingOrder", errorMessage)

                return result.data
            }
        }


    }


    fun getVarients(pageNumber: Int = 1) {
        if (pageNumber == 1 && _varients.value != null) return;
        viewModelScope.launch(Dispatchers.Default + _coroutinExption) {
            var result = homeRepository.getVarient(pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var varientolder = result.data as List<VarientResponseDto>

                    var mutableVarient = mutableListOf<VarientModel>()

                    if (pageNumber != 1 && _varients.value != null) {
                        mutableVarient.addAll(_varients.value!!.toList())
                    }
                    if (varientolder.isNotEmpty())
                        mutableVarient.addAll(
                            varientolder.map { it.toVarient() }.toList()
                        )

                    if (mutableVarient.isNotEmpty()) {
                        _varients.emit(
                            mutableVarient
                        )
                    } else {
                        if (_varients.value == null)
                            _varients.emit(mutableListOf())
                    }
                }

                else -> {}
            }
        }
    }


    fun logout() {
        viewModelScope.launch(Dispatchers.IO + _coroutinExption) {
            dao.nukeTable()
        }
    }

}


typealias  d = String;
fun main() {
    val name:d = "ahmed"
}


