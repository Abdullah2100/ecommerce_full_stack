package com.example.eccomerce_app.model

import com.example.eccomerce_app.dto.response.AddressResponseDto
import com.example.eccomerce_app.dto.response.CategoryReponseDto

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

    fun CategoryReponseDto.toCategory(): Category{
        return Category(
            id= this.id,
            name =this.name,
            image=this.image_path.replace("localhost","10.0.2.2")
        );
    }
}