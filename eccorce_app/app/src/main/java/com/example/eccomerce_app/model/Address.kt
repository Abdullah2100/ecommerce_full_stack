package com.example.eccomerce_app.model

import java.util.UUID

data class Address(
    val id: UUID?,
    val longitude: Double,
    val latitude: Double,
    val title: String?,
    val isCurrnt: Boolean
)
