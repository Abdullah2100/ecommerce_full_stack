package com.example.e_commercompose.model

import java.util.UUID

data class ProductVarientSelection(
    val name: String,
    val percentage: Double?,
    val variantId: UUID,
    )
