package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class Delivery(
    val id: UUID,
    val userId: UUID,
    val isAvailable: Boolean,
    val thumbnail: String? = null,
    val address: Address,
    val user: UserModel,
    val analys: DeliveryAnalyse?=null
)
data class DeliveryAnalyse(
    val dayFee: Double? = null,
    val weekFee: Double? = null,
    val monthFee: Double? = null,
    val dayOrder: Int? = null,
    val weekOrder: Int? = null
)



