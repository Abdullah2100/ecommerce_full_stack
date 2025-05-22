package com.example.eccomerce_app.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable()
data class ProductVarientReponseDto(
    @Serializable(with = UUIDKserialize::class)
    var id: UUID,
    var name:String,
    var precentage: Double,

    @Serializable(with = UUIDKserialize::class)
    var varient_id: UUID,
    )
