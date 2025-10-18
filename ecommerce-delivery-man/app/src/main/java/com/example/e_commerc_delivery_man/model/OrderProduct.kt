package com.example.eccomerce_app.model

import java.util.UUID

data class OrderProduct(
    val id: UUID,
    val name: String,
    val thmbnail: String?,
    val storeId: UUID
)
