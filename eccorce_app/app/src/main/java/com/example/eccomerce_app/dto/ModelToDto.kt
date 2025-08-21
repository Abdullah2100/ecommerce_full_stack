package com.example.e_commercompose.dto

import com.example.e_commercompose.model.CardProductModel
import com.example.e_commercompose.model.CartModel
import com.example.e_commercompose.model.ProductVariant
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.eccomerce_app.dto.CreateOrderDto
import com.example.eccomerce_app.dto.CreateOrderItemDto
import com.example.eccomerce_app.dto.CreateProductVarientDto
import com.example.eccomerce_app.dto.SubCategoryDto
import com.example.eccomerce_app.dto.UpdateSubCategoryDto

object ModelToDto {



    fun SubCategoryUpdate.toUpdateSubCategoryDto():UpdateSubCategoryDto{
        return UpdateSubCategoryDto(
            Name=this.name,
            Id=this.id,
            CateogyId=this.cateogyId
        )
    }

    fun ProductVarientSelection.toProdcutVarientRequestDto(): CreateProductVarientDto{
        return CreateProductVarientDto(
            Name = this.name,
            Precentage = this.percentage,
            VarientId =this.variantId
        )
    }
    fun List<List<ProductVariant>>.toListOfProductVarient(): List<ProductVarientSelection> {
        return   this.map{it->it.map {
                data->
            ProductVarientSelection(name = data.name, percentage = data.percentage, variantId = data.variantId)

        }}.flatten()
    }

    fun CardProductModel.toOrderRequestItemDto(): CreateOrderItemDto{
        return CreateOrderItemDto(
            StoreId =  this.storeId,
            ProductId = this.productId,
            Price = this.price,
            Quantity = this.quantity,
            ProductsVarientId =  this.productVariants.map { it.id }
        )
    }

    fun CartModel.toOrderRequestDto(): CreateOrderDto{
        return CreateOrderDto(
            Items = this.cartProducts.map { it.toOrderRequestItemDto() },
            TotalPrice = this.totalPrice ,
            Latitude = this.latitude,
            Longitude=this.longitude
        )
    }

    fun SubCategoryUpdate.toSubCategoryUpdateDto(): SubCategoryDto{
        return SubCategoryDto(
            Id=this.id,
            CategoryId = this.cateogyId,
            Name = this.name,
        )
    }
}