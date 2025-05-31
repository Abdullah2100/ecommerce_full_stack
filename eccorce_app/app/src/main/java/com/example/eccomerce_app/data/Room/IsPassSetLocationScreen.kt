package com.example.e_commercompose.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "location")
data class IsPassSetLocationScreen(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val default: Boolean=false
)
