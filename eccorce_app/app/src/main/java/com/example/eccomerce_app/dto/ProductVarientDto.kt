package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable()
data class ProductVarientDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val name: String,
    val precentage: Double,
    @Serializable(with = UUIDKserialize::class)
    val varientId: UUID,
    )

@Serializable
data class CreateProductVarientDto(
    val Name: String,
    val Precentage: Double?,
    @Serializable(with= UUIDKserialize::class)
    val VarientId: UUID,
)
