package com.example.eccomerce_app.model

import java.util.UUID

data class Banner(
    var id: UUID,
    var image: String,
    val store_id: UUID
)
