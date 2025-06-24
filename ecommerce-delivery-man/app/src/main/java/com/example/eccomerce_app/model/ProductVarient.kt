package com.example.e_commerc_delivery_man.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class ProductVarient(
    val id: UUID,
    val name:String,
    val precentage: Double,
    val varient_id: UUID,
    )
