package com.example.eccomerce_app.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderVarient(
    val varient_name:String,
    val product_varient_name:String
)
