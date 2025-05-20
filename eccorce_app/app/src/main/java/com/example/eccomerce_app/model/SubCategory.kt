package com.example.eccomerce_app.model

import java.util.UUID

data class SubCategory(
    var id: UUID,
    var name: String,
    var category_id: UUID,
    var store_id: UUID

)
