package com.example.eccomerce_app.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SubCategoryUpdateDto(
    var name: String,
    @Serializable(with = UUIDKserialize::class)
    var id: UUID,
    @Serializable(with = UUIDKserialize::class)
    var cateogy_id: UUID
)
