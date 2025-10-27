package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
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
    val productVariants:List<List<ProductVarientDto>>?=null,
    val productImages:List<String>

)