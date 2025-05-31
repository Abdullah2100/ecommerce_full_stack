package com.example.eccomerce_app.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderItem(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val price: Double,
    val quanity:Int,
    val product: OrderProduct,
    val productVarient:List<OrderVarient>
)
