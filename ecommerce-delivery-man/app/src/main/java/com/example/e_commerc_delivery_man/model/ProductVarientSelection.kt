package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class ProductVarientSelection(
    val name: String,
    val precentage: Double?,
    val varient_id: UUID,
    )
