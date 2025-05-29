package com.example.eccomerce_app.model

import java.io.File

data class MyInfoUpdate(
    val name:String?=null,
    val thumbnail: File?=null,
    val newPassword:String?=null,
    val oldPassword:String?=null,
    val phone:String?=null
)