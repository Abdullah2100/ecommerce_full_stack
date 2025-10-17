package com.example.e_commercompose.model

import java.util.UUID

data class OrderItem(
    val id: UUID,
    val orderId: UUID,
    val price: Double,
    val quantity:Int,
    val product: OrderProduct,
    val productVariant:List<OrderVariant>,
    val orderItemStatus: String,
    val orderStatusName:String
)
