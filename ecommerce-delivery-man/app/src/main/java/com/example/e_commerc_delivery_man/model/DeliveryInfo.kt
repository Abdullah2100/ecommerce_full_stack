package com.example.e_commerc_delivery_man.model

import com.example.e_commerc_delivery_man.dto.response.AddressResponseDto
import com.example.e_commerc_delivery_man.dto.response.DeliveryAnalysDto
import com.example.e_commerc_delivery_man.dto.response.UserDto
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class DeliveryInfo(
    val id: UUID,
    val userId: UUID,
    val isAvaliable: Boolean,
    val thumbnail: String? = null,
    val address: Address,
    val analys: DeliveryAnalys,
    val user: UserModel
)
