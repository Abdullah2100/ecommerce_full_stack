package com.example.e_commerc_delivery_man.dto

import com.example.e_commerc_delivery_man.model.Address

object ModelToDto {
    fun Address.toAddressDto(): AddressDto {
        return AddressDto(
            latitude = this.latitude,
            longitude = this.longitude,
        )
    }


}