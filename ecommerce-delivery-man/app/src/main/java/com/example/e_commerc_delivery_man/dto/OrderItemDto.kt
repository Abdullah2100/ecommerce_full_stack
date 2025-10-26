package com.example.eccomerce_app.dto.response

import com.example.e_commerc_delivery_man.dto.AddressWithTitleDto
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderItemDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val price: Double?=null,
    val Quantity:Int?=null,
    val address: List<AddressWithTitleDto>?=null,
    val product: OrderProductDto?=null,
    val productVariant:List<OrderVariantDto>?=null,
    val orderItemStatus: String?=null
)

@Serializable()
data class OrderItemStatusChangeDto(
    @Serializable(with = UUIDKserialize::class)
    val orderId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val orderItemId: UUID,
    val status: String
)
@Serializable()
data class OrderItemsStatusEvent(
    @Serializable(with = UUIDKserialize::class)
    val orderId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val orderItemId: UUID,
    val Status: String
)
