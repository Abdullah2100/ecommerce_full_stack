package com.example.e_commercompose.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CategoryReponseDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val name: String,
    val image_path: String
)
