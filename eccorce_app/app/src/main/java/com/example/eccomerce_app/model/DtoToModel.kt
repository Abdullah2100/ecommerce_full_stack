package com.example.e_commercompose.model

import com.example.eccomerce_app.dto.AddressDto
import com.example.eccomerce_app.dto.BannerDto
import com.example.eccomerce_app.dto.CategoryDto
import com.example.eccomerce_app.dto.ProductDto
import com.example.eccomerce_app.dto.ProductVarientDto
import com.example.eccomerce_app.dto.StoreDto
import com.example.eccomerce_app.dto.SubCategoryDto
import com.example.eccomerce_app.dto.UserDto
import com.example.eccomerce_app.dto.VarientDto
import com.example.eccomerce_app.dto.GeneralSettingDto
import com.example.eccomerce_app.dto.OrderItemDto
import com.example.eccomerce_app.dto.OrderProductDto
import com.example.eccomerce_app.dto.OrderDto
import com.example.eccomerce_app.dto.OrderVarientDto
import kotlin.text.replace

object DtoToModel {
    fun AddressDto.toAddress(): Address {
        return Address(
            id = this.Id,
            title =this.Title,
            latitude = this.Latitude,
            longitude = this.Lngitude,
            isCurrnt = this.IsCurrent?:false
        )
    }

    fun CategoryDto.toCategory(): Category{
        return Category(
            id= this.Id,
            name =this.Name,
            image=this.Image.replace("localhost","10.0.2.2")
        );
    }
    fun SubCategoryDto.toSubCategory(): SubCategory{
        return SubCategory(
            id = this.Id,
            name=this.Name,
            category_id=this.CategoryId,
            store_id=this.StoreId)
    }


    fun StoreDto.toStore():StoreModel{
        return StoreModel(
            id = this.Id,
            name = this.Name,
            user_id = this.UserId,
            pig_image = this.WallpaperImage.replace("localhost", "10.0.2.2"),
            small_image = this.SmallImage.replace("localhost", "10.0.2.2"),
            latitude = this.Latitude,
            longitude = this.Longitude
        )
    }

    fun UserDto.toUser(): UserModel{
        return UserModel(
            id = this.Id,
            name = this.Name,
            phone = this.Phone,
            email = this.Email,
            thumbnail=if(!this.Thumbnail.isEmpty())this.Thumbnail.replace("localhost","10.0.2.2") else "",
            address = this.Address?.map { it.toAddress() }?.toList(),
            store_id = this.StoreId
        )
    }


    fun BannerDto.toBanner():BannerModel{
        return BannerModel(
            id=this.Id,
            image=if(this.Image.isNotEmpty())this.Image.replace("localhost","10.0.2.2") else "",
            store_id = this.StoreId
        )
    }

    fun VarientDto.toVarient(): VarientModel{
        return VarientModel(
            id =  this.Id,
            name=this.Name
        )
    }

    fun ProductVarientDto.toProductVarient(): ProductVarient {
        return ProductVarient(
            id = this.Id,
            name=this.Name,
            precentage = this.Precentage,
            varient_id =this.VarientId
        )
    }

    fun ProductDto.toProdcut(): ProductModel {
        return ProductModel(
            id = this.Id,
            name = this.Name,
            description = this.Description,
            thmbnail = if(this.Thmbnail.isNotEmpty())this.Thmbnail.replace("localhost","10.0.2.2") else "",
            subcategory_id = this.SubcategoryId,
            store_id = this.StoreId,
            price = this.Price,
            category_id = this.CategoryId,
            productVarients = this.ProductVarients?.map {
                it.map { it.toProductVarient() }
            },
            productImages = this.ProductImages.map { it->if(it.isNotEmpty())it.replace("localhost","10.0.2.2") else "" }
        )
    }

    fun OrderVarientDto.toOrderVarient(): OrderVarient{
        return OrderVarient(
            varient_name=this.VarientName,
            product_varient_name=this.ProductVarientName
        )
    }

    fun OrderProductDto.toOrderProduct(): OrderProduct{
        return OrderProduct(
            id= this.Id,
            name = this.Name,
            thmbnail = if(this.Thmbnail.isNotEmpty())this.Thmbnail.replace("localhost","10.0.2.2") else ""
        )
    }

    fun OrderItemDto.toOrderItem(): OrderItem{
        return OrderItem(
            id=this.Id,
            quanity= this.Quanity,
            price = this.Price,
            product = this.Product.toOrderProduct(),
            productVarient = this.ProductVarient.map { it.toOrderVarient() },
             orderItemStatus = this.OrderItemStatus
        )
    }

    fun OrderDto.toOrderItem():Order{
       return Order(
          id = this.Id,
           latitude = this.Latitude,
           longitude = this.Longitude,
           deliveryFee = this.DeliveryFee,
           user_phone = this.UserPhone,
           totalPrice=this.TotalPrice,
           order_items = this.OrderItems.map { it.toOrderItem() },
           status = this.Status
       )
    }

    fun GeneralSettingDto.toGeneralSetting(): GeneralSetting{
        return GeneralSetting(
            id = this.Id,
            name=this.Name,
            value =this.Value
        )
    }

}