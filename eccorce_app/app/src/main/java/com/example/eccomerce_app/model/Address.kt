package com.example.eccomerce_app.model

import java.util.UUID

data class Address(
    var id: UUID?,
    var longitude: Double,
    var latitude: Double,
    var title: String,
    var isCurrnt: Boolean
)
