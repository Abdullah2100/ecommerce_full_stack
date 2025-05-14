package com.example.eccomerce_app.model

import com.example.eccomerce_app.dto.response.AddressResponseDto
import com.example.eccomerce_app.dto.response.CategoryReponseDto
import com.example.eccomerce_app.dto.response.UserDto

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

    fun UserDto.toUser(): User{
        return User(
            id = this.id,
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail=if(this.thumbnail!=null)this.thumbnail.replace("localhost","10.0.2.2") else "",
            address = this.address?.map { it.toAddress() }?.toList()
        )
    }
}