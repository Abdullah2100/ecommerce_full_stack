package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import com.example.hotel_mobile.services.kSerializeChanger.UUIDListKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderItemDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val price: Double,
    val quanity:Int,
    val product: OrderProductDto,
    val productVarient:List<OrderVarientDto>?=null,
    val orderItemStatus: String
)


@Serializable
data class CreateOrderItemDto(
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID,
    val Price: Double,
    val Quantity: Int,
    @Serializable(with = UUIDKserialize::class)
    val ProductId: UUID,
    @Serializable(with = UUIDListKserialize::class)
    val ProductsVarientId: List<UUID>,
)


@Serializable()
data class OrderItemsStatusEvent(
    @Serializable(with = UUIDKserialize::class)
    val OrderId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val OrderItemId: UUID,
    val Status: String
)

@Serializable
data class UpdateOrderItemStatusDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    val Status:Int
)
