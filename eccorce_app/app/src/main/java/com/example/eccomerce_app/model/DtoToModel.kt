package com.example.e_commercompose.model

import com.example.e_commercompose.dto.response.AddressResponseDto
import com.example.e_commercompose.dto.response.BannerResponseDto
import com.example.e_commercompose.dto.response.CategoryReponseDto
import com.example.e_commercompose.dto.response.ProductResponseDto
import com.example.e_commercompose.dto.response.ProductVarientReponseDto
import com.example.e_commercompose.dto.response.StoreResposeDto
import com.example.e_commercompose.dto.response.SubCategoryResponseDto
import com.example.e_commercompose.dto.response.UserDto
import com.example.e_commercompose.dto.response.VarientResponseDto
import com.example.e_commercompose.dto.response.GeneralSettingResponseDto
import com.example.e_commercompose.dto.response.OrderItemResponseDto
import com.example.e_commercompose.dto.response.OrderProductResponseDto
import com.example.e_commercompose.dto.response.OrderResponseDto
import com.example.e_commercompose.dto.response.OrderVarientResponseDto
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
            image=this.image.replace("localhost","10.0.2.2")
        );
    }
    fun SubCategoryResponseDto.toSubCategory(): SubCategory{
        return SubCategory(
            id = this.id,
            name=this.name,
            category_id=this.categoryId,
            store_id=this.storeId)
    }


    fun StoreResposeDto.toStore():StoreModel{
        return StoreModel(
            id = this.id,
            name = this.name,
            user_id = this.userId,
            pig_image = this.wallpaperImage.replace("localhost", "10.0.2.2"),
            small_image = this.smallImage.replace("localhost", "10.0.2.2"),
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
            thumbnail=if(this.thumbnail!=null)this.thumbnail.replace("localhost","10.0.2.2") else "",
            address = this.address?.map { it.toAddress() }?.toList(),
            store_id = this.storeId
        )
    }


    fun BannerResponseDto.toBanner():BannerModel{
        return BannerModel(
            id=this.id,
            image=if(this.image.isNotEmpty())this.image.replace("localhost","10.0.2.2") else "",
            store_id = this.storeId
        )
    }

    fun VarientResponseDto.toVarient(): VarientModel{
        return VarientModel(
            id =  this.id,
            name=this.name
        )
    }

    fun ProductVarientReponseDto.toProductVarient(): ProductVarient {
        return ProductVarient(
            id = this.id,
            name=this.name,
            precentage = this.precentage,
            varient_id =this.varientId
        )
    }

    fun ProductResponseDto.toProdcut(): ProductModel {
        return ProductModel(
            id = this.id,
            name = this.name,
            description = this.description,
            thmbnail = if(this.thmbnail.isNotEmpty())this.thmbnail.replace("localhost","10.0.2.2") else "",
            subcategory_id = this.subcategoryId,
            store_id = this.storeId,
            price = this.price,
            category_id = this.categoryId,
            productVarients = this.productVarients?.map {
                it.map { it.toProductVarient() }
            },
            productImages = this.productImages.map { it->if(it.isNotEmpty())it.replace("localhost","10.0.2.2") else "" }
        )
    }

    fun OrderVarientResponseDto.toOrderVarient(): OrderVarient{
        return OrderVarient(
            varient_name=this.varientName,
            product_varient_name=this.productVarientName
        )
    }

    fun OrderProductResponseDto.toOrderProduct(): OrderProduct{
        return OrderProduct(
            id= this.id,
            name = this.name,
            thmbnail = if(this.thmbnail.isNotEmpty())this.thmbnail.replace("localhost","10.0.2.2") else ""
        )
    }

    fun OrderItemResponseDto.toOrderItem(): OrderItem{
        return OrderItem(
            id=this.id,
            quanity= this.quanity,
            price = this.price,
            product = this.product.toOrderProduct(),
            productVarient = this.productVarient.map { it.toOrderVarient() },
             orderItemStatus = this.orderItemStatus
        )
    }

    fun OrderResponseDto.toOrderItem():Order{
       return Order(
          id = this.id,
           latitude = this.latitude,
           longitude = this.longitude,
           deliveryFee = this.deliveryFee,
           user_phone = this.userPhone,
           totalPrice=this.totalPrice,
           order_items = this.orderItems.map { it.toOrderItem() },
           status = this.status
       )
    }

    fun GeneralSettingResponseDto.toGeneralSetting(): GeneralSetting{
        return GeneralSetting(
            id = this.id,
            name=this.name,
            value =this.value
        )
    }

}