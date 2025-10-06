package com.example.e_commerc_delivery_man.ui

import com.example.e_commerc_delivery_man.model.enMapType
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
    object MyOrder




    @Serializable
    object Address

    @Serializable
    data class Map(
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
    object LocationHome

    @Serializable
    object Orders



}