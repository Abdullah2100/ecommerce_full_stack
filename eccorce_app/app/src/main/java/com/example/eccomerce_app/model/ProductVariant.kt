package com.example.e_commercompose.model

import java.util.UUID

data class ProductVariant(
    val id: UUID,
    val name:String,
    val percentage: Double,
    val variantId: UUID,
    )
