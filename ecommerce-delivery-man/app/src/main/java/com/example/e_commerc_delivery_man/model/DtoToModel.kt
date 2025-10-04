package com.example.e_commerc_delivery_man.model

import com.example.e_commerc_delivery_man.dto.response.AddressResponseDto
import com.example.e_commerc_delivery_man.dto.response.DeliveryResponseDto
import com.example.e_commerc_delivery_man.dto.response.UserDto
import com.example.e_commercompose.dto.response.VarientResponseDto
import com.example.e_commercompose.model.VarientModel
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
            latitude = this.latitude,
            longitude = this.longitude,
        )
    }

    fun UserDto.toUser(): UserModel{
        return UserModel(
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail=if(this.thumbnail.isNullOrEmpty())"" else this.thumbnail.replace("localhost","10.0.2.2"),
        )
    }


    fun DeliveryResponseDto.toDeliveryInfo(): Delivery{
        return Delivery(
            id = this.id,
            userId = this.userId,
            isAvailable = this.isAvailable,
            thumbnail=if(this.thumbnail.isNullOrEmpty())"" else this.thumbnail.replace("localhost","10.0.2.2"),
            address= this.address.toAddress(),
            user = this.user.toUser()

            )
    }

    fun VarientResponseDto.toVarient(): VarientModel{
        return VarientModel(
            id =  this.id,
            name=this.name
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
            storeId = this.storeId,
            thmbnail = if(this.thmbnail.isNotEmpty())this.thmbnail.replace("localhost","10.0.2.2") else ""
        )
    }

    fun OrderItemResponseDto.toOrderItem(): OrderItem{
        return OrderItem(
            id=this.id,
            quanity= this.quanity,
            price = this.price,
            product = this.product.toOrderProduct(),
            address = this.address?.toAddress(),
            productVarient = this.productVarient.map { it.toOrderVarient() },
             orderItemStatus = this.orderItemStatus
        )
    }

    fun OrderResponseDto.toOrder():Order{
       return Order(
           id = this.id,
           latitude = this.latitude,
           longitude = this.longitude,
           user_phone = this.userPhone,
           name = this.name,
           deliveryFee = this.deliveryFee,
           totalPrice = this.totalPrice,

           orderItems = this.orderItems.map { it.toOrderItem() },
           status = this.status,
           userPhone = this.userPhone,
           realPrice = this.totalPrice
       )
    }


}