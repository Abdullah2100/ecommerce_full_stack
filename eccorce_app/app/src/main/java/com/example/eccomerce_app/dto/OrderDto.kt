package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import com.example.hotel_mobile.services.kSerializeChanger.UUIDListKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    val Longitude: Double,
    val Latitude: Double,
    val TotalPrice: Double,
    val DeliveryFee: Double,
    val UserPhone: String,
    val Status:Int,
    val OrderItems:List<OrderItemDto>
)

@Serializable
data class CreateOrderDto(
    val Longitude: Double,
    val Latitude: Double,
    val TotalPrice: Double,
    val Items: List<CreateOrderItemDto>,
)
@Serializable
data class OrderRequestItemsDto(
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID,
    val Price: Double,
    val Quantity: Int,
    @Serializable(with = UUIDKserialize::class)
    val ProductId: UUID,
    @Serializable(with = UUIDListKserialize::class)
    val ProductsVarientId: List<UUID>,
)
