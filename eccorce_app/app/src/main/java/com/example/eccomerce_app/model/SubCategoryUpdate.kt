package com.example.e_commercompose.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class SubCategoryUpdate(
    val name: String,
    val id: UUID,
    val cateogy_id: UUID,
)
