package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class StoreModel(
    val id: UUID,
    val user_id: UUID,
    val name: String,
    val pig_image: String,
    val small_image: String,
    val longitude: Double,
    val latitude: Double,
)
