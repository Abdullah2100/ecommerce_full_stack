package com.example.e_commercompose.dto

import com.example.e_commercompose.dto.request.CartRequestDto
import com.example.e_commercompose.dto.request.OrderRequestItemsDto
import com.example.e_commercompose.dto.request.ProductVarientRequestDto
import com.example.e_commercompose.dto.request.SubCategoryUpdateDto
import com.example.e_commercompose.model.CardProductModel
import com.example.e_commercompose.model.CartModel
import com.example.e_commercompose.model.ProductVarient
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.model.SubCategoryUpdate

object ModelToDto {

//    fun Address.toLocationRequestDto(): AddressRequestDto {
//        return AddressRequestDto(
//            latitude=this.latitude,
//            longitude = this.longitude,
//            title = this.title?:"",
//        )
//    }

    fun SubCategoryUpdate.toSubCategoryUpdateDto():SubCategoryUpdateDto{
        return SubCategoryUpdateDto(
            name=this.name,
            id=this.id,
            cateogy_id=this.cateogy_id
        )
    }

    fun ProductVarientSelection.toProdcutVarientRequestDto(): ProductVarientRequestDto{
        return ProductVarientRequestDto(
            name = this.name,
            precentage = this.precentage,
            varient_id =this.varient_id
        )
    }
    fun List<List<ProductVarient>>.toListOfProductVarient(): List<ProductVarientSelection> {
        return   this.map{it->it.map {
                data->
            ProductVarientSelection(name = data.name, precentage = data.precentage, varient_id = data.varient_id)

        }}.flatten()
    }

    fun CardProductModel.toOrderRequestItemDto(): OrderRequestItemsDto{
        return OrderRequestItemsDto(
            storeId =  this.store_id,
            productId = this.productId,
            price = this.price,
            quanity = this.quantity,
            productsVarientId =  this.productvarients.map { it.id }
        )
    }

    fun CartModel.toOrderRequestDto():CartRequestDto{
        return CartRequestDto(
            items = this.cartProducts.map { it.toOrderRequestItemDto() },
            totalPrice = this.totalPrice ,
            latitude = this.latitu,
            longitude=this.longit
        )
    }
}