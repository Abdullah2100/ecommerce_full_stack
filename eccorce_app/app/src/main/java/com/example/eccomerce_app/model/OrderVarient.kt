package com.example.e_commercompose.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderVarient(
    val varient_name:String,
    val product_varient_name:String
)
