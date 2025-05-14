package com.example.eccomerce_app.model

import com.example.eccomerce_app.dto.response.AddressResponseDto
import java.util.UUID

data class User(
    var id: UUID,
    var name:String,
    var phone: String,
    var email: String,
    var thumbnail:String,
    var address:List<Address>?=null
)
