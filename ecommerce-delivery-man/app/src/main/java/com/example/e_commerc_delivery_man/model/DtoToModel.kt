package com.example.e_commerc_delivery_man.model

import com.example.e_commerc_delivery_man.dto.AddressDto
import com.example.e_commerc_delivery_man.dto.AddressWithTitleDto
import com.example.e_commerc_delivery_man.dto.DeliveryAnalysDto
import com.example.e_commerc_delivery_man.dto.DeliveryDto
import com.example.e_commerc_delivery_man.dto.UserDto
import com.example.e_commercompose.dto.response.VariantDto
import com.example.e_commercompose.model.VariantModel
import com.example.eccomerce_app.dto.response.OrderItemDto
import com.example.eccomerce_app.dto.response.OrderProductDto
import com.example.eccomerce_app.dto.response.OrderDto
import com.example.eccomerce_app.dto.response.OrderVarientDto
import com.example.eccomerce_app.model.Order
import com.example.eccomerce_app.model.OrderItem
import com.example.eccomerce_app.model.OrderProduct
import com.example.eccomerce_app.model.OrderVarient
import kotlin.text.replace

object DtoToModel {
    fun AddressDto.toAddress(): Address {
        return Address(
            latitude = this.latitude,
            longitude = this.longitude,
        )
    }
    fun AddressWithTitleDto.toAddressWithTitle(): AddressWithTitle {
        return AddressWithTitle(
            latitude = this.latitude,
            longitude = this.longitude,
            title = this.title
        )
    }
    fun DeliveryAnalysDto.toDeliveryAnalyse(): DeliveryAnalyse {
        return DeliveryAnalyse(
            dayFee = this.dayFee,
            weekFee = this.weekFee,
            monthFee = this.monthFee,
            dayOrder = this.dayOrder,
            weekOrder = this.weekOrder
        )
    }

    fun UserDto.toUser(): UserModel {
        return UserModel(
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail = if (this.thumbnail.isNullOrEmpty()) "" else this.thumbnail.replace(
                "0.0.0.0",
                "192.168.1.45"
            ),
        )
    }


    fun DeliveryDto.toDeliveryInfo(): Delivery {
        return Delivery(
            id = this.id,
            userId = this.userId,
            isAvailable = this.isAvailable,
            thumbnail = if (this.thumbnail.isNullOrEmpty()) "" else this.thumbnail.replace(
                "0.0.0.0",
                "192.168.1.45"
            ),
            address = this.address.toAddress(),
            user = this.user.toUser(),
            analys = this.analys?.toDeliveryAnalyse()
        )
    }

    fun VariantDto.toVarient(): VariantModel {
        return VariantModel(
            id = this.id,
            name = this.name
        )
    }

    fun OrderVarientDto.toOrderVarient(): OrderVarient {
        return OrderVarient(
            varient_name = this.varientName,
            product_varient_name = this.productVarientName
        )
    }

    fun OrderProductDto.toOrderProduct(): OrderProduct {
        return OrderProduct(
            id = this.id,
            name = this.name,
            storeId = this.storeId,
            thmbnail = if (!this.thmbnail.isNullOrEmpty()) this.thmbnail.replace(
                "0.0.0.0",
                "192.168.1.45"
            ) else ""
        )
    }

    fun OrderItemDto.toOrderItem(): OrderItem {
        return OrderItem(
            id = this.id,
            quanity = this.quanity?:0,
            price = this.price?:0.0,
            product = this.product?.toOrderProduct(),
            address = this.address?.map { it.toAddressWithTitle() },
            productVarient = this.productVarient?.map { it.toOrderVarient() },
            orderItemStatus = this.orderItemStatus?:""
        )
    }

    fun OrderDto.toOrder(): Order {
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
        )
    }


}