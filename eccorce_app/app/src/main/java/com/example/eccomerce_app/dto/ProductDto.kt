package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductDto(
    @Serializable(with= UUIDKserialize::class)
    val Id: UUID,
    val Name: String,
    val Description:String,
    val Thmbnail:String,
    @Serializable(with = UUIDKserialize::class)
    val SubcategoryId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val CategoryId: UUID,
    val Price: Double,
    val ProductVarients:List<List<ProductVarientDto>>?=null,
    val ProductImages:List<String>

)