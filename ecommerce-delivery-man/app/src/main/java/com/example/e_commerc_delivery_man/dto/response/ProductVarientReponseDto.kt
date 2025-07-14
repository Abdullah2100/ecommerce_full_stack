package com.example.e_commerc_delivery_man.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable()
data class ProductVarientReponseDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val name:String,
    val precentage: Double,

    @Serializable(with = UUIDKserialize::class)
    val varient_id: UUID,

    )
