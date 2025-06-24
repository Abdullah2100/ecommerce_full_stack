package com.example.e_commerc_delivery_man.util

object Secrets {
    init {
        System.loadLibrary("keys") // Load the library once
    }

    external fun getBaseUrl(): String
}