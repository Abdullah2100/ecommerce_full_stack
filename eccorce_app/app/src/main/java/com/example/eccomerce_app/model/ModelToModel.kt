package com.example.eccomerce_app.model

object ModelToModel {

    fun List<List<ProductVarient>>.toListOfProductVarient(): List<ProductVarientSelection> {
     return   this.map{it->it.map {
         data->
            ProductVarientSelection(name = data.name, precentage = data.precentage, varient_id = data.varient_id)

        }}.flatten()
    }
}