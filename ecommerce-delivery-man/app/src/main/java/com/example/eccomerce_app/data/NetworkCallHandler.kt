package com.example.hotel_mobile.Modle

sealed class NetworkCallHandler () {
data class Successful<out T>(val data:T):NetworkCallHandler();
    data class Error(val data: String?):NetworkCallHandler();
}