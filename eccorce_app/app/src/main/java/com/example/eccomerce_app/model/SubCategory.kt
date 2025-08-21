package com.example.e_commercompose.model

import java.util.UUID

data class SubCategory(
    val id: UUID,
    val name: String,
    val categoryId: UUID,
    val storeId: UUID
)
