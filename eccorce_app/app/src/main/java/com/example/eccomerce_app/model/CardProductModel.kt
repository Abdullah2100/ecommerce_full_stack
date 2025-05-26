package com.example.eccomerce_app.model

import java.util.UUID

data class CardProductModel(
    var id: UUID,
    var productId: UUID,
    var name: String,
    var thmbnail:String,
    var store_id: UUID,
    var price: Double,
    var productVarients:List<ProductVarient>,
    var quantity:Int=1
) ;