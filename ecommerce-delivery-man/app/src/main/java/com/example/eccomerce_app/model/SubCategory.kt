package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class SubCategory(
    val id: UUID,
    val name: String,
    val category_id: UUID,
    val store_id: UUID

)
