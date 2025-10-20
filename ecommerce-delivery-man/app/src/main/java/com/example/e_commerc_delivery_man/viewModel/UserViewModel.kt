package com.example.e_commerc_delivery_man.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerc_delivery_man.util.General
import com.example.e_commerc_delivery_man.data.Room.ILocationDao
import com.example.e_commerc_delivery_man.data.Room.IsSetLocation
import com.example.e_commerc_delivery_man.data.repository.UserRepository
import com.example.e_commerc_delivery_man.dto.DeliveryDto
import com.example.e_commerc_delivery_man.model.Address
import com.example.e_commerc_delivery_man.model.Delivery
import com.example.e_commerc_delivery_man.model.DtoToModel.toDeliveryInfo
import com.example.e_commerc_delivery_man.model.UpdateMyInfo
import com.example.hotel_mobile.Modle.NetworkCallHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


class UserViewModel(
    private val userRepository: UserRepository,
    private val locationDao: ILocationDao
) : ViewModel() {
    private var _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }

    private var _myInfo = MutableStateFlow<Delivery?>(null)
    var myInfo = _myInfo.asStateFlow()

    private var _isPassLocation = MutableStateFlow<Boolean?>(null)
    var isPassLocation = _isPassLocation.asStateFlow()

    init {
        isPassLocation()
        getMyInfo()
    }


    fun getMyInfo(isUpdated: Boolean = false) {
        if (_myInfo.value != null && isUpdated == false) return;
        viewModelScope.launch(Dispatchers.Default + _coroutineException) {
            val result = userRepository.getMyInfo();
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as DeliveryDto
                    _myInfo.emit(data.toDeliveryInfo())

                }

                is NetworkCallHandler.Error -> {
                    if (_myInfo.value == null) {
                        _myInfo.emit(
                            null
                        )
                    }
                    val resultError = result.data as String
                    Log.d("errorFromNetowrk", resultError)
                }


            }
        }
    }

    suspend fun updateDeliveryStatus(
        status: Boolean
    ): String? {
        val result = userRepository.updateDeliveryState(
            status
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as Boolean
                val updatedInfo = _myInfo.value?.copy(isAvailable = status)
                _myInfo.emit(updatedInfo)
                return null;
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "").toString()

            }

        }
    }


    suspend fun updateDeliveryInfo(
        address: Address? = null,
        thumbnail: File? = null,
        userInfo: UpdateMyInfo? = null
    ): String? {
        val result = userRepository.updateDeliveryInfo(
            address = address,
            thumbnail = thumbnail,
            userInfo = userInfo
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as DeliveryDto
                val deliveryInfo = data.toDeliveryInfo()
                val userCopy = _myInfo.value?.copy(user =deliveryInfo.user,thumbnail = deliveryInfo.thumbnail, address = deliveryInfo.address)
                _myInfo.emit(userCopy)

                return null;
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "").toString()

            }

        }
    }


    suspend fun userPassLocation(status: Boolean) {
        locationDao.savedPassLocation(isSetLocation = IsSetLocation(0, status))
    }

    fun isPassLocation() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            val result = locationDao.isPassLocationScreen()
            _isPassLocation.emit(result)

        }
    }
}