package com.example.e_commercompose.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductResponseDto(
    @Serializable(with= UUIDKserialize::class)
    val id: UUID,
    val name: String,
    val description:String,
    val thmbnail:String,
    @Serializable(with = UUIDKserialize::class)
    val subcategoryId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val storeId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val categoryId: UUID,
    val price: Double,
    val productVarients:List<List<ProductVarientReponseDto>>?=null,
    val productImages:List<String>

) ;