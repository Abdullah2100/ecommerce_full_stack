package com.example.eccomerce_app.model

import com.example.e_commerc_delivery_man.model.AddressWithTitle
import java.util.UUID

data class OrderItem(
    val id: UUID,
    val price: Double,
    val quantity:Int,
    val address: List<AddressWithTitle>?=null,
    val product: OrderProduct?=null,
    val productVariant:List<OrderVarient>?=null,
    val orderItemStatus: String
)
