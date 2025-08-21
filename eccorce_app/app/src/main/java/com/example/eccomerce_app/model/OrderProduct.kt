package com.example.e_commercompose.model

import java.util.UUID

data class OrderProduct(
    val id: UUID,
    val name:String,
    val thumbnail: String
)
