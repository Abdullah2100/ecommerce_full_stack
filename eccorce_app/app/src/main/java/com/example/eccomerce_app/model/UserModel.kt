package com.example.eccomerce_app.model

import java.util.UUID

data class UserModel(
    var id: UUID,
    var name: String,
    var phone: String,
    var email: String,
    var thumbnail: String,
    var address: List<Address>? = null,
    val store_id: UUID
)
