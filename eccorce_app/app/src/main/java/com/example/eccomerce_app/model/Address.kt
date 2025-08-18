package com.example.e_commercompose.model

import java.util.UUID

data class Address(
    val id: UUID?,
    val longitude: Double,
    val latitude: Double,
    val title: String?,
    val isCurrnt: Boolean
)

enum class enMapType{My,MyStore,Store,TrackOrder}
