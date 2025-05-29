package com.example.eccomerce_app.dto.response

import androidx.compose.runtime.Composable
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
    val subcategory_id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val store_id: UUID,
    val price: Double,
    val productVarients:List<List<ProductVarientReponseDto>>?=null,
    val productImages:List<String>

) ;