package com.example.eccomerce_app.dto.request

import androidx.compose.runtime.Composable
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductVarientRequestDto(
    var name: String,
    var precentage: Double?,
    @Serializable(with= UUIDKserialize::class)
    var varient_id: UUID,
    )
