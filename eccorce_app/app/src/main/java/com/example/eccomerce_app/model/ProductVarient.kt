package com.example.eccomerce_app.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class ProductVarient(
    var id: UUID,
    var name:String,
    var precentage: Double,
    var varient_id: UUID,
    )
