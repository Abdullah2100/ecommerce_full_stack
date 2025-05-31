package com.example.e_commercompose.model

import java.util.UUID

data class StoreModel(
    val id: UUID,
    val user_id: UUID,
    val name: String,
    val pig_image: String,
    val small_image: String,
)
