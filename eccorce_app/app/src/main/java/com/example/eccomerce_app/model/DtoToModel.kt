package com.example.e_commercompose.model

import com.example.eccomerce_app.dto.AddressDto
import com.example.eccomerce_app.dto.BannerDto
import com.example.eccomerce_app.dto.CategoryDto
import com.example.eccomerce_app.dto.DeliveryDto
import com.example.eccomerce_app.dto.DeliveryUserInfoDto
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
import com.example.eccomerce_app.model.Delivery
import com.example.eccomerce_app.model.DeliveryUserInfo
import com.example.eccomerce_app.util.Secrets
import kotlin.text.replace

object DtoToModel {
    fun AddressDto.toAddress(): Address {
        return Address(
            id = this.id,
            title = this.title,
            latitude = this.latitude,
            longitude = this.longitude,
            isCurrent = this.isCurrent ?: false
        )
    }

    fun CategoryDto.toCategory(): Category {
        return Category(
            id = this.id,
            name = this.name,
            image = this.image.replace("0.0.0.0", Secrets.imageUrl)
        )
    }

    fun SubCategoryDto.toSubCategory(): SubCategory {
        return SubCategory(
            id = this.id,
            name = this.name,
            categoryId = this.categoryId,
            storeId = this.storeId
        )
    }


    fun StoreDto.toStore(): StoreModel {
        return StoreModel(
            id = this.id,
            name = this.name,
            userId = this.userId,
            pigImage = this.wallpaperImage.replace("0.0.0.0", Secrets.imageUrl),
            smallImage = this.smallImage.replace("0.0.0.0", Secrets.imageUrl),
            latitude = this.latitude,
            longitude = this.longitude
        )
    }

    fun UserDto.toUser(): UserModel {
        return UserModel(
            id = this.id,
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail = if (!this.thumbnail.isEmpty()) this.thumbnail.replace(
                "0.0.0.0",
                Secrets.imageUrl
            ) else "",
            address = this.address?.map { it.toAddress() }?.toList(),
            storeId = this.storeId
        )
    }


    fun DeliveryUserInfoDto.toDeliveryUserInfo(): DeliveryUserInfo {
        return DeliveryUserInfo(
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail = if (this.thumbnail.isNullOrEmpty()) "" else this.thumbnail.replace(
                "localhost",
                Secrets.imageUrl
            ),
        )
    }

    fun DeliveryDto.toDeliveryInfo(): Delivery {
        return Delivery(
            id = this.id,
            userId = this.userId,
            isAvailable = this.isAvailable,
            thumbnail = if (this.thumbnail.isNullOrEmpty()) "" else this.thumbnail.replace(
                "localhost",
                "10.0.2.2"
            ),
            address = this.address?.toAddress(),
            user = this.user.toDeliveryUserInfo()

        )

    }

    fun BannerDto.toBanner(): BannerModel {
        return BannerModel(
            id = this.id,
            image = if (this.image.isNotEmpty()) this.image.replace(
                "0.0.0.0",
                Secrets.imageUrl
            ) else "",
            storeId = this.storeId
        )
    }

    fun VarientDto.toVarient(): VarirntModel {
        return VarirntModel(
            id = this.Id,
            name = this.Name
        )
    }

    fun ProductVarientDto.toProductVarient(): ProductVariant {
        return ProductVariant(
            id = this.id,
            name = this.name,
            percentage = this.precentage,
            variantId = this.varientId
        )
    }

    fun ProductDto.toProdcut(): ProductModel {
        return ProductModel(
            id = this.id,
            name = this.name,
            description = this.description,
            thumbnail = if (this.thmbnail.isNotEmpty()) this.thmbnail.replace(
                "0.0.0.0",
                Secrets.imageUrl
            ) else "",
            subcategoryId = this.subcategoryId,
            storeId = this.storeId,
            price = this.price,
            categoryId = this.categoryId,
            productVariants = this.productVarients?.map {
                it.map { it.toProductVarient() }
            },
            productImages = this.productImages.map { it ->
                if (it.isNotEmpty()) it.replace(
                    "0.0.0.0",
                    Secrets.imageUrl
                ) else ""
            }
        )
    }

    fun OrderVarientDto.toOrderVarient(): OrderVariant {
        return OrderVariant(
            variantName = this.varientName,
            productVariantName = this.productVarientName
        )
    }

    fun OrderProductDto.toOrderProduct(): OrderProduct {
        return OrderProduct(
            id = this.id,
            name = this.name,
            thumbnail = if (this.thmbnail != null && this.thmbnail.isNotEmpty()) this.thmbnail.replace(
                "0.0.0.0",
                Secrets.imageUrl
            ) else ""
        )
    }

    fun OrderItemDto.toOrderItem(): OrderItem {
        return OrderItem(
            id = this.id,
            quantity = this.quanity,
            price = this.price,
            product = this.product.toOrderProduct(),
            productVariant = if (this.productVarient.isNullOrEmpty())
                listOf()
            else this.productVarient.map { it.toOrderVarient() },
            orderItemStatus = this.orderItemStatus
        )
    }

    fun OrderDto.toOrderItem(): Order {
        return Order(
            id = this.id,
            latitude = this.latitude,
            longitude = this.longitude,
            deliveryFee = this.deliveryFee,
            userPhone = this.userPhone,
            totalPrice = this.totalPrice,
            orderItems = this.orderItems.map { it.toOrderItem() },
            status = this.status
        )
    }

    fun GeneralSettingDto.toGeneralSetting(): GeneralSetting {
        return GeneralSetting(
            name = this.name,
            value = this.value
        )
    }

}