package com.example.eccomerce_app.model

import com.example.e_commerc_delivery_man.model.AddressWithTitle
import java.util.UUID

data class OrderItem(
    val id: UUID,
    val price: Double,
    val quanity:Int,
    val address: List<AddressWithTitle>?=null,
    val product: OrderProduct,
    val productVarient:List<OrderVarient>?=null,
    val orderItemStatus: String
)
