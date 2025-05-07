package com.example.eccomerce_app.util

object Secrets {
    init {
        System.loadLibrary("keys") // Load the library once
    }

    external fun getBaseUrl(): String
}