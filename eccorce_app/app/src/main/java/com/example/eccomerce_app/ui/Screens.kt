package com.example.eccomerce_app.ui
import kotlinx.serialization.Serializable

object Screens {

    @Serializable
    object OnBoarding

    @Serializable
    object AuthGraph

    @Serializable
    object  Login

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





}