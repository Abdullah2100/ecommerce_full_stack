package com.example.e_commercompose.model

import java.util.UUID

data class CardProductModel(
    val id: UUID,
    val productId: UUID,
    val name: String,
    val thumbnail:String,
    val storeId: UUID,
    val price: Double,
    val productVariants:List<ProductVariant>,
    val quantity:Int=1
) 