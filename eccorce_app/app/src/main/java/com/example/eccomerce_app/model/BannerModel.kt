package com.example.eccomerce_app.model

import java.util.UUID

data class BannerModel(
    val id: UUID,
    val image: String,
    val store_id: UUID
)
