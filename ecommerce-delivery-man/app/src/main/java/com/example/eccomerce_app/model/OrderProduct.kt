package com.example.eccomerce_app.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderProduct(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val name:String,
    val thmbnail: String
)
