package com.example.e_commerc_delivery_man.model

import java.io.File

data class UserModel(
    val name: String,
    val phone: String,
    val email: String,
    val thumbnail: String?,
)



data class UpdateMyInfo(
    val name:String?=null,
    val thumbnail: File?=null,
    val newPassword:String?=null,
    val oldPassword:String?=null,
    val phone:String?=null
)
