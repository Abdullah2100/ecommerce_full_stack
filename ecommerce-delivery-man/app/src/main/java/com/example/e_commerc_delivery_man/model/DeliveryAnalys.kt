package com.example.e_commerc_delivery_man.model

import kotlinx.serialization.Serializable

data class DeliveryAnalys(
    val dayFee: Double? = null,
    val weekFee: Double? = null,
    val monthFee: Double? = null,
    val dayOrder: Int? = null,
    val weekOrder: Int? = null
)
