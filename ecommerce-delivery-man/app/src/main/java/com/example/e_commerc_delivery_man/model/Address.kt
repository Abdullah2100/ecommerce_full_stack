package com.example.e_commerc_delivery_man.model

import kotlinx.serialization.Serializable
import java.util.UUID

data class Address(
    val longitude: Double,
    val latitude: Double,
)

@Serializable
data class AddressWithTitle(
    val longitude: Double,
    val latitude: Double,
    val title: String?=null
)
enum class enMapType{My,TrackOrder}
