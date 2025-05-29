package com.example.eccomerce_app.model

import java.util.UUID

data class SubCategory(
    val id: UUID,
    val name: String,
    val category_id: UUID,
    val store_id: UUID

)
