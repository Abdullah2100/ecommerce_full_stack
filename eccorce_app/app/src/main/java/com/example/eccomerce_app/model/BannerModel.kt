package com.example.e_commercompose.model

import java.util.UUID

data class BannerModel(
    val id: UUID,
    val image: String,
    val storeId: UUID
)
