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
import com.example.eccomerce_app.dto.response.OrderItemResponseDto
import com.example.eccomerce_app.dto.response.OrderProductResponseDto
import com.example.eccomerce_app.dto.response.OrderResponseDto
import com.example.eccomerce_app.dto.response.OrderVarientResponseDto
import com.example.eccomerce_app.model.Order
import com.example.eccomerce_app.model.OrderItem
import com.example.eccomerce_app.model.OrderProduct
import com.example.eccomerce_app.model.OrderVarient
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


    fun StoreResposeDto.toStore():StoreModel{
        return StoreModel(
            id = this.id,
            name = this.name,
            user_id = this.user_id,
            pig_image = this.wallpaper_image.replace("localhost", "10.0.2.2"),
            small_image = this.small_image.replace("localhost", "10.0.2.2"),
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
            store_id = this.store_id
        )
    }


    fun BannerResponseDto.toBanner():BannerModel{
        return BannerModel(
            id=this.id,
            image=if(this.image.isNotEmpty())this.image.replace("localhost","10.0.2.2") else "",
            store_id = this.store_id
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
            varient_id =this.varient_id
        )
    }

    fun ProductResponseDto.toProdcut(): ProductModel {
        return ProductModel(
            id = this.id,
            name = this.name,
            description = this.description,
            thmbnail = if(this.thmbnail.isNotEmpty())this.thmbnail.replace("localhost","10.0.2.2") else "",
            subcategory_id = this.subcategory_id,
            store_id = this.store_id,
            price = this.price,
            productVarients = this.productVarients?.map {
                it.map { it.toProductVarient() }
            },
            productImages = this.productImages.map { it->if(it.isNotEmpty())it.replace("localhost","10.0.2.2") else "" }
        )
    }

    fun OrderVarientResponseDto.toOrderVarient(): OrderVarient{
        return OrderVarient(
            varient_name=this.varient_name,
            product_varient_name=this.product_varient_name
        )
    }

    fun OrderProductResponseDto.toOrderProduct(): OrderProduct{
        return OrderProduct(
            id= this.id,
            name = this.name,
            thmbnail = if(this.thmbnail.isNotEmpty())this.thmbnail.replace("localhost","10.0.2.2") else ""
        )
    }

    fun OrderItemResponseDto.toOrderResponse(): OrderItem{
        return OrderItem(
            id=this.id,
            quanity= this.quanity,
            price = this.price,
            product = this.product.toOrderProduct(),
            productVarient = this.productVarient.map { it.toOrderVarient() }
        )
    }

    fun OrderResponseDto.toOrder():Order{
       return Order(
          id = this.id,
           latitude = this.latitude,
           longitude = this.longitude,
           user_phone = this.user_phone,
           order_items = this.order_items.map { it.toOrderResponse() },
           status = this.status
       )
    }

}