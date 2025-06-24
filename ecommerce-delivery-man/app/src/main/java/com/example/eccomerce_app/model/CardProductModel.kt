package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class CardProductModel(
    val id: UUID,
    val productId: UUID,
    val name: String,
    val thmbnail:String,
    val store_id: UUID,
    val price: Double,
    val productvarients:List<ProductVarient>,
    val quantity:Int=1
) ;