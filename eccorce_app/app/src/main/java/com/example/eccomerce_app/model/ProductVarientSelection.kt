package com.example.eccomerce_app.model

import java.util.UUID

data class ProductVarientSelection(
    val name: String,
    val precentage: Double?,
    val varient_id: UUID,
    )
