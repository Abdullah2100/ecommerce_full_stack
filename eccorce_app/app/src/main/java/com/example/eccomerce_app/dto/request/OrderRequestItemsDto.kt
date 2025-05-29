package com.example.eccomerce_app.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import com.example.hotel_mobile.services.kSerializeChanger.UUIDListKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderRequestItemsDto(
    @Serializable(with = UUIDKserialize::class)
    val store_id: UUID,
    val price: Double,
    val quanity: Int,
    @Serializable(with = UUIDKserialize::class)
    val product_Id: UUID,
    @Serializable(with = UUIDListKserialize::class)
    val products_varient_id: List<UUID>

)
