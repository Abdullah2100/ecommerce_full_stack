package com.example.e_commerc_delivery_man.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SubCategoryUpdateDto(
    val name: String,
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val cateogy_id: UUID
)
