package com.example.e_commerc_delivery_man.dto

import com.example.e_commerc_delivery_man.dto.request.AddressRequestDto
import com.example.e_commerc_delivery_man.dto.request.CartRequestDto
import com.example.e_commerc_delivery_man.dto.request.OrderRequestItemsDto
import com.example.e_commerc_delivery_man.dto.request.ProductVarientRequestDto
import com.example.e_commerc_delivery_man.dto.request.SubCategoryUpdateDto
import com.example.e_commerc_delivery_man.model.Address
import com.example.e_commerc_delivery_man.model.CardProductModel
import com.example.e_commerc_delivery_man.model.CartModel
import com.example.e_commerc_delivery_man.model.ProductVarient
import com.example.e_commerc_delivery_man.model.ProductVarientSelection
import com.example.e_commerc_delivery_man.model.SubCategoryUpdate

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