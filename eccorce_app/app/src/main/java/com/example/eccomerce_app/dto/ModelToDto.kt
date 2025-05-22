package com.example.eccomerce_app.dto

import com.example.eccomerce_app.dto.request.LocationRequestDto
import com.example.eccomerce_app.dto.request.ProductVarientRequestDto
import com.example.eccomerce_app.dto.request.SubCategoryUpdateDto
import com.example.eccomerce_app.model.Address
import com.example.eccomerce_app.model.ProductVarientSelection
import com.example.eccomerce_app.model.SubCategoryUpdate

object ModelToDto {

    fun Address.toLocationRequestDto(): LocationRequestDto {
        return LocationRequestDto(
            id = this.id,
            latitude=this.latitude,
            longitude = this.longitude,
            title = this.title?:"",
        )
    }

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
            varient_id=this.varient_id
        )
    }

}