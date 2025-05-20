package com.example.eccomerce_app.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class SubCategoryUpdate(
    var name: String,
    var id: UUID,
    var cateogy_id: UUID,
)
