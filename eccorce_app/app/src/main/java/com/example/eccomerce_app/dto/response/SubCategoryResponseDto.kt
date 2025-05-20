package com.example.eccomerce_app.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SubCategoryResponseDto(
    var name: String,
    @Serializable(with = UUIDKserialize::class)
    var id: UUID,
    @Serializable(with = UUIDKserialize::class)
    var category_id: UUID,
    @Serializable(with = UUIDKserialize::class)
    var store_id: UUID
)
