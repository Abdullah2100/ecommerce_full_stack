package com.example.e_commerc_delivery_man.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class IsSetLocation(
    @PrimaryKey()
    val id: Int? = 0,
    val condition: Boolean = false
)
