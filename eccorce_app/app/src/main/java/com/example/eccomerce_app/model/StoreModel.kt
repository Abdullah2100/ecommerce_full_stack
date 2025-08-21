package com.example.e_commercompose.model

import java.util.UUID

data class StoreModel(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val pigImage: String,
    val smallImage: String,
    val longitude: Double,
    val latitude: Double,
)
