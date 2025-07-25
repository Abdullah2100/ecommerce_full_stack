package com.example.e_commercompose.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SubCategoryRequestDto(
  val    name:String,
  @Serializable(with= UUIDKserialize::class)
 val cateogy_id: UUID
)
