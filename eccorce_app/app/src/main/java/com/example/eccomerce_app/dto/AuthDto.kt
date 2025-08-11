package com.example.eccomerce_app.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val Username: String,
    val Password: String,
    val DeviceToken: String
)

@Serializable
data class SignupDto(
    val Name: String,
    val Phone: String,
    val Email: String,
    val Password: String,
    val DeviceToken: String
)

@Serializable
data class AuthDto(
    val token: String="",
    val refreshToken: String = ""
)


@Serializable
data class ForgetPasswordDto(val Email: String)


@Serializable
data class CreateVerificationDto(
    val Email:String,
    val Otp: String
)


@Serializable
data class CreateReseatPasswordDto(
    val Email: String,
    val Otp: String,
    val Password: String
)

