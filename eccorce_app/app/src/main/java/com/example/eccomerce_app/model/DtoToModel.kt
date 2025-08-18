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
import java.util.UUID
import kotlin.text.replace

object DtoToModel {
    fun AddressDto.toAddress(): Address {
        return Address(
            id = this.id,
            title =this.title,
            latitude = this.latitude,
            longitude = this.longitude,
            isCurrnt = this.isCurrent?:false
        )
    }

    fun CategoryDto.toCategory(): Category{
        return Category(
            id= this.id,
            name =this.name,
            image=this.image.replace("0.0.0.0","192.168.1.45")
        );
    }
    fun SubCategoryDto.toSubCategory(): SubCategory{
        return SubCategory(
            id = this.Id,
            name=this.Name,
            category_id=this.CategoryId,
            storeId= UUID.randomUUID()
        )
    }


    fun StoreDto.toStore():StoreModel{
        return StoreModel(
            id = this.id,
            name = this.name,
            user_id = this.userId,
            pig_image = this.wallpaperImage.replace("0.0.0.0", "192.168.1.45"),
            small_image = this.smallImage.replace("0.0.0.0", "192.168.1.45"),
            latitude = this.latitude,
            longitude = this.longitude
        )
    }

    fun UserDto.toUser(): UserModel{
        return UserModel(
            id = this.id,
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail=if(!this.thumbnail.isEmpty())this.thumbnail.replace("0.0.0.0","192.168.1.45") else "",
            address = this.address?.map { it.toAddress() }?.toList(),
            store_id = this.storeId
        )
    }


    fun BannerDto.toBanner():BannerModel{
        return BannerModel(
            id=this.id,
            image=if(this.image.isNotEmpty())this.image.replace("0.0.0.0","192.168.1.45") else "",
            store_id = this.storeId
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
            thmbnail = if(this.Thmbnail.isNotEmpty())this.Thmbnail.replace("0.0.0.0","192.168.1.45") else "",
            subcategory_id = this.SubcategoryId,
            store_id = this.StoreId,
            price = this.Price,
            category_id = this.CategoryId,
            productVarients = this.ProductVarients?.map {
                it.map { it.toProductVarient() }
            },
            productImages = this.ProductImages.map { it->if(it.isNotEmpty())it.replace("0.0.0.0","192.168.1.45") else "" }
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
            thmbnail = if(this.Thmbnail.isNotEmpty())this.Thmbnail.replace("0.0.0.0","192.168.1.45") else ""
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