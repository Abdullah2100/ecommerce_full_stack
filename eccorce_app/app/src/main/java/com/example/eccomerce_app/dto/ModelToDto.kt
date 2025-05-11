package com.example.eccomerce_app.dto

import com.example.eccomerce_app.dto.request.LocationRequestDto
import com.example.eccomerce_app.model.Address

object ModelToDto {

    fun Address.toLocationRequestDto(): LocationRequestDto {
        return LocationRequestDto(
            id = this.id,
            latitude=this.latitude,
            longitude = this.longitude,
            title = this.title,
        )
    }
}