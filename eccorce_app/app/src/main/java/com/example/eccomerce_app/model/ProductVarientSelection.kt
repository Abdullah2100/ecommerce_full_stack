package com.example.eccomerce_app.model

import java.util.UUID

data class ProductVarientSelection(
    var name: String,
    var precentage: Double?,
    var varient_id: UUID,
    )
