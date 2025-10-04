package com.example.eccomerce_app.model

import com.example.e_commercompose.model.Address
import com.example.eccomerce_app.dto.AddressDto
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID


data class Delivery(
    val id: UUID,
    val userId: UUID,
    val isAvailable: Boolean,
    val thumbnail: String? = null,
    val address: Address?,
    val user:DeliveryUserInfo
)
@Serializable
data class DeliveryUserInfo(
    val name: String,
    val phone: String,
    val email: String,
    val thumbnail: String?=null,
)
