package com.example.eccomerce_app.model

import java.io.File

data class MyInfoUpdate(
    var name:String?=null,
    var thumbnail: File?=null,
    var newPassword:String?=null,
    var oldPassword:String?=null,
    var phone:String?=null
)