package com.example.eccomerce_app.dto.response

import androidx.compose.runtime.Composable
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductResponseDto(
    @Serializable(with= UUIDKserialize::class)
    var id: UUID,
    var name: String,
    var description:String,
    var thmbnail:String,
    @Serializable(with = UUIDKserialize::class)
    var subcategory_id: String,
    @Serializable(with = UUIDKserialize::class)
    var store_id: UUID,
    var price: Double,
    var productVarients:List<List<ProductVarientReponseDto>>?=null,
    var productImages:List<String>

) ;