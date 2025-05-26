package com.example.eccomerce_app.model

import java.util.UUID

data class StoreModel(
    var id: UUID,
    var user_id: UUID,
    var name: String,
    var pig_image: String,
    var small_image: String,
)
