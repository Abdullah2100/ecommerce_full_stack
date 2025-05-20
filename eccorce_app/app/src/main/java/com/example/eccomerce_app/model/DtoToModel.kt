package com.example.eccomerce_app.model

import com.example.eccomerce_app.dto.response.AddressResponseDto
import com.example.eccomerce_app.dto.response.BannerResponseDto
import com.example.eccomerce_app.dto.response.CategoryReponseDto
import com.example.eccomerce_app.dto.response.StoreResposeDto
import com.example.eccomerce_app.dto.response.SubCategoryResponseDto
import com.example.eccomerce_app.dto.response.UserDto
import kotlin.text.replace

object DtoToModel {
    fun AddressResponseDto.toAddress(): Address {
        return Address(
            id = this.id,
            title =this.title,
            latitude = this.latitude,
            longitude = this.longitude,
            isCurrnt = this.isCurrent?:false
        )
    }

    fun CategoryReponseDto.toCategory(): Category{
        return Category(
            id= this.id,
            name =this.name,
            image=this.image_path.replace("localhost","10.0.2.2")
        );
    }
    fun SubCategoryResponseDto.toSubCategory(): SubCategory{
        return SubCategory(
            id = this.id,
            name=this.name,
            category_id=this.category_id,
            store_id=this.store_id)
    }





    fun StoreResposeDto.toStore():Store{
        return Store(
            id = this.id,
            name = this.name,
            user_id = this.user_id,
            pig_image = this.wallpaper_image.replace("localhost", "10.0.2.2"),
            small_image = this.small_image.replace("localhost", "10.0.2.2"),
        )
    }

    fun UserDto.toUser(): User{
        return User(
            id = this.id,
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail=if(this.thumbnail!=null)this.thumbnail.replace("localhost","10.0.2.2") else "",
            address = this.address?.map { it.toAddress() }?.toList(),
            store_id = this.store_id
        )
    }


    fun BannerResponseDto.toBanner():Banner{
        return Banner(
            id=this.id,
            image=if(this.image.isNotEmpty())this.image.replace("localhost","10.0.2.2") else "",
            store_id = this.store_id
        )
    }
}