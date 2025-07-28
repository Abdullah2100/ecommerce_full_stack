package com.example.e_commercompose.dto

import com.example.e_commercompose.model.CardProductModel
import com.example.e_commercompose.model.CartModel
import com.example.e_commercompose.model.ProductVarient
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.eccomerce_app.dto.CreateOrderDto
import com.example.eccomerce_app.dto.CreateOrderItemDto
import com.example.eccomerce_app.dto.CreateProductVarientDto
import com.example.eccomerce_app.dto.UpdateSubCategoryDto

object ModelToDto {



    fun SubCategoryUpdate.toUpdateSubCategoryDto():UpdateSubCategoryDto{
        return UpdateSubCategoryDto(
            Name=this.name,
            Id=this.id,
            CateogyId=this.cateogy_id
        )
    }

    fun ProductVarientSelection.toProdcutVarientRequestDto(): CreateProductVarientDto{
        return CreateProductVarientDto(
            Name = this.name,
            Precentage = this.precentage,
            VarientId =this.varientId
        )
    }
    fun List<List<ProductVarient>>.toListOfProductVarient(): List<ProductVarientSelection> {
        return   this.map{it->it.map {
                data->
            ProductVarientSelection(name = data.name, precentage = data.precentage, varientId = data.varient_id)

        }}.flatten()
    }

    fun CardProductModel.toOrderRequestItemDto(): CreateOrderItemDto{
        return CreateOrderItemDto(
            StoreId =  this.store_id,
            ProductId = this.productId,
            Price = this.price,
            Quantity = this.quantity,
            ProductsVarientId =  this.productvarients.map { it.id }
        )
    }

    fun CartModel.toOrderRequestDto(): CreateOrderDto{
        return CreateOrderDto(
            Items = this.cartProducts.map { it.toOrderRequestItemDto() },
            TotalPrice = this.totalPrice ,
            Latitude = this.latitu,
            Longitude=this.longit
        )
    }
}