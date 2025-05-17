package com.example.eccomerce_app.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class Store(
    var id: UUID,
    var user_id: UUID,
    var name: String,
    var pig_image: String,
    var small_image: String,
    var subcategory:List<SubCategory>?=null,
    var user: User?,
    var addresses:List<Address>
)
