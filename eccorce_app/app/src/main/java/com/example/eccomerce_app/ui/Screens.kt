package com.example.eccomerce_app.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

object Screens {

    @Serializable
    object OnBoarding

    @Serializable
    object AuthGraph

    @Serializable
    object Login

    @Serializable
    object Signup

    @Serializable
    object ReseatPassword


    @Serializable
    object LocationGraph

    @Serializable
    object LocationHome


    @Serializable
    data class LocationList(var isFromLocationHome: Boolean)

    @Serializable
    object HomeGraph

    @Serializable
    object Home

    @Serializable
    object Account

    @Serializable
    object Profile


    @Serializable
    data class Store(
        var store_idCopy: String? = null,
        var isFromHome: Boolean? = true
    )


    @Serializable
    data class CreateProduct(
        var store_id: String,
        var product_id: String?=null
    )

    @Serializable
    data class ProductDetails(
        var product_Id: String,
        var isFromHome: Boolean
    )

    @Serializable
    object Cart

    @Serializable
    object Checkout

    @Serializable
    object Address

    @Serializable
    object Map
}