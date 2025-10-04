package com.example.eccomerce_app.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.e_commercompose.model.DtoToModel.toDeliveryInfo
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.Model.AuthModelEntity
import com.example.eccomerce_app.dto.LoginDto
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.dto.AuthDto
import com.example.eccomerce_app.dto.SignupDto
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.AuthRepository
import com.example.eccomerce_app.data.repository.DeliveryRepository
import com.example.eccomerce_app.dto.DeliveryDto
import com.example.eccomerce_app.model.Delivery
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID


class DeliveryViewModel(
    private val deliveryRepository: DeliveryRepository,
) : ViewModel() {

    val deliveries = MutableStateFlow<List<Delivery>?>(null)
    suspend fun createDelivery(userId: UUID): String? {
        val result = deliveryRepository.createNewDelivery(userId)
        return when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as? DeliveryDto
                val newDeliveriesList = mutableListOf<Delivery>()
                if (data != null) {
                    newDeliveriesList.add(data.toDeliveryInfo())
                }
                if (deliveries.value != null)
                    newDeliveriesList.addAll(deliveries.value!!)

                deliveries.emit(newDeliveriesList)

                null;
            }

            is NetworkCallHandler.Error -> {
                return result.data
            }
        }
    }
}
