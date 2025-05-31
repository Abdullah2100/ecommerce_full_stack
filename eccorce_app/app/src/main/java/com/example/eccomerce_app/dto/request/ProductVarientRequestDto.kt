package com.example.e_commercompose.dto.request

import androidx.compose.runtime.Composable
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductVarientRequestDto(
    val name: String,
    val precentage: Double?,
    @Serializable(with= UUIDKserialize::class)
    val varient_id: UUID,
    )
