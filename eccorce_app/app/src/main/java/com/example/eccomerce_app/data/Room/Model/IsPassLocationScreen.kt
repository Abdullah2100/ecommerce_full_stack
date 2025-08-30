package com.example.eccomerce_app.data.Room.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class IsPassLocationScreen(
    @PrimaryKey
    var id: Int = 0,
    var condition: Boolean
)