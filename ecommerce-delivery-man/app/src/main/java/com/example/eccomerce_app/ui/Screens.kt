package com.example.e_commerc_delivery_man.ui

import kotlinx.serialization.Serializable

object Screens {




    @Serializable
    object Login
    

    @Serializable
    object ReseatPasswordGraph

    @Serializable
    object  GenerateOtp



    @Serializable
    data class LocationList(var isFromLocationHome: Boolean)

    @Serializable
    object HomeGraph

    @Serializable
    object Home

    @Serializable
    object Category

    @Serializable
    data class ProductCategory(val cateogry_id: String)

    @Serializable
    object Account

    @Serializable
    object Profile


    @Serializable
    data class Store(
        var store_id: String? = null,
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
    object MyOrder


    @Serializable
    object Checkout

    @Serializable
    object Address

    @Serializable
    object Map

    @Serializable
    object Orders

    @Serializable
    object OrderForMyStore

}