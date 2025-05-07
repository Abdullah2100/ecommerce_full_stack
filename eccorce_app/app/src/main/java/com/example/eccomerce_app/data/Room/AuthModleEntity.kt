package com.example.eccomerce_app.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class AuthModleEntity
    (
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val token: String = "",
    val refreshToken: String = ""
)