package com.example.e_commercompose.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StoreStatusResponseDto(
    @Serializable(with = UUIDKserialize::class)
    val storeId: UUID,
    val status: Boolean
)
