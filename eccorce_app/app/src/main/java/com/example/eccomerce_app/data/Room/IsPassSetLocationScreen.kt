package com.example.eccomerce_app.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class IsPassSetLocationScreen(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val default: Boolean=false
)
