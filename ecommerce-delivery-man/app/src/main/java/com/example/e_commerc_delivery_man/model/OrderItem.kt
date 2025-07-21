package com.example.eccomerce_app.model

import com.example.e_commerc_delivery_man.model.Address
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class OrderItem(
    val id: UUID,
    val price: Double,
    val quanity:Int,
    val address: Address?,
    val product: OrderProduct,
    val productVarient:List<OrderVarient>,
    val orderItemStatus: String

)
