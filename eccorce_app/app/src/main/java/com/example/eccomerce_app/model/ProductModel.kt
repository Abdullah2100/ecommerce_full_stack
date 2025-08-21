package com.example.e_commercompose.model

import java.util.UUID

data class ProductModel(
    val id: UUID,
    val name: String,
    val description:String,
    val thumbnail:String,
    val subcategoryId: UUID,
    val storeId: UUID,
    val categoryId: UUID,
    val price: Double,
    val productVariants:List<List<ProductVariant>>?=null,
    val productImages:List<String>
) 