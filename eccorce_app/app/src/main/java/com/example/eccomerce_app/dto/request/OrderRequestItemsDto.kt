package com.example.e_commercompose.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import com.example.hotel_mobile.services.kSerializeChanger.UUIDListKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderRequestItemsDto(
    @Serializable(with = UUIDKserialize::class)
    val storeId: UUID,
    val price: Double,
    val quanity: Int,
    @Serializable(with = UUIDKserialize::class)
    val productId: UUID,
    @Serializable(with = UUIDListKserialize::class)
    val productsVarientId: List<UUID>,
)
