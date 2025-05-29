package com.example.eccomerce_app.dto

import com.example.eccomerce_app.dto.request.AddressRequestDto
import com.example.eccomerce_app.dto.request.CartRequestDto
import com.example.eccomerce_app.dto.request.OrderRequestItemsDto
import com.example.eccomerce_app.dto.request.ProductVarientRequestDto
import com.example.eccomerce_app.dto.request.SubCategoryUpdateDto
import com.example.eccomerce_app.model.Address
import com.example.eccomerce_app.model.CardProductModel
import com.example.eccomerce_app.model.CartModel
import com.example.eccomerce_app.model.ProductVarient
import com.example.eccomerce_app.model.ProductVarientSelection
import com.example.eccomerce_app.model.SubCategoryUpdate

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
            store_id =  this.store_id,
            product_Id = this.productId,
            price = this.price,
            quanity = this.quantity,
            products_varient_id =  this.productvarients.map { it.id }
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