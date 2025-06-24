package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class UserModel(
    val id: UUID,
    val name: String,
    val phone: String,
    val email: String,
    val thumbnail: String,
    val address: List<Address>? = null,
    val store_id: UUID
)
