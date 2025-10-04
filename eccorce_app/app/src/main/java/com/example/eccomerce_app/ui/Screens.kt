package com.example.eccomerce_app.ui

import com.example.e_commercompose.model.enMapType
import kotlinx.serialization.Serializable

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
    object  PickCurrentAddress

    @Serializable
    object HomeGraph

    @Serializable
    object Home

    @Serializable
    object Category

    @Serializable
    data class ProductCategory(val categoryId: String)

    @Serializable
    object Account

    @Serializable
    object Profile


    @Serializable
    data class Store(
        val storeId: String? = null,
        val isFromHome: Boolean? = true
    )

    @Serializable
    object DeliveriesList


    @Serializable
    data class CreateProduct(
        val storeId: String,
        val productId: String? = null
    )

    @Serializable
    data class ProductDetails(
        val productId: String,
        val isFromHome: Boolean,
        val isCanNavigateToStore: Boolean
    )


    @Serializable
    data class MapScreen(
        val title:String?=null,
        val id:String?=null,
        val lognit: Double?=null,
        val latitt: Double?=null,
        val additionLong: Double? = null,
        val additionLat: Double? = null,
        val mapType: enMapType,
        val isFromLogin: Boolean
    )

    @Serializable
    object Cart

    @Serializable
    object Checkout

    @Serializable
    object EditeOrAddNewAddress

    @Serializable
    object Order

    @Serializable
    object OrderForMyStore

}