package com.example.e_commercompose.ui

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
    object ReseatPasswordGraph

    @Serializable
    object GenerateOtp

    @Serializable
    data class OtpVerification(val email: String)

    @Serializable
    data class ReseatPassword(val email: String, val otp: String)

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
        var product_id: String? = null
    )

    @Serializable
    data class ProductDetails(
        var product_Id: String,
        var isFromHome: Boolean
    )


    @Serializable
    data class MapScreen(
        var title:String?=null,
        val id:String?=null,
        val lognit: Double?,
        val latitt: Double?,
        val isFromLogin: Boolean
    )

    @Serializable
    object Cart

    @Serializable
    object Checkout

    @Serializable
    object Address

    @Serializable
    object Map

    @Serializable
    object Order

    @Serializable
    object OrderForMyStore

}