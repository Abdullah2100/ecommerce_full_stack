package com.example.eccomerce_app.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class OrderProduct(
    val id: UUID,
    val name:String,
    val thmbnail: String,
    val storeId: UUID
)
