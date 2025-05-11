package com.example.eccomerce_app.model

import com.example.eccomerce_app.dto.response.AddressResponseDto

object DtoToModel {
    fun AddressResponseDto.toAddress(): Address {
        return Address(
            id = this.id,
            title =this.title,
            latitude = this.latitude,
            longitude = this.longitude,
            isCurrnt = this.isCurrnt?:false
        )
    }
}