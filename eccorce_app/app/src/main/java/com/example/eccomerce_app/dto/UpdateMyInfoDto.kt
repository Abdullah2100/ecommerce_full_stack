package com.example.eccomerce_app.dto

import java.io.File

data class UpdateMyInfoDto(
    val name:String?=null,
    val thumbnail: File?=null,
    val newPassword:String?=null,
    val oldPassword:String?=null,
    val phone:String?=null
)