package com.example.e_commerc_delivery_man.dto.response

import kotlinx.serialization.Serializable

@Serializable()
data class DeliveryAnalysDto(
    val dayFee: Double? = null,
    val weekFee: Double? = null,
    val monthFee: Double? = null,
    val dayOrder: Int? = null,
    val weekOrder: Int? = null
)
