package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class BannerModel(
    val id: UUID,
    val image: String,
    val store_id: UUID
)
