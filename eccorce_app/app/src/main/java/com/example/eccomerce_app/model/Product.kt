package com.example.eccomerce_app.model

import java.util.UUID

data class Product(
    var id: UUID,
    var name: String,
    var description:String,
    var thmbnail:String,
    var subcategory_id: UUID,
    var store_id: UUID,
    var price: Double,
    var productVarients:List<List<ProductVarient>>?=null,
    var productImages:List<String>
) ;