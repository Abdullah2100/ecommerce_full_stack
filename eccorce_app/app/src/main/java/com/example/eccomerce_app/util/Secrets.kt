package com.example.e_commercompose.util

object Secrets {
    init {
        System.loadLibrary("keys") // Load the library once
    }

    external fun getBaseUrl(): String
}