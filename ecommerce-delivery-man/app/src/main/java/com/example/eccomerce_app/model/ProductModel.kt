package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class ProductModel(
    val id: UUID,
    val name: String,
    val description:String,
    val thmbnail:String,
    val subcategory_id: UUID,
    val store_id: UUID,
    val category_id: UUID,
    val price: Double,
    val productVarients:List<List<ProductVarient>>?=null,
    val productImages:List<String>
) ;