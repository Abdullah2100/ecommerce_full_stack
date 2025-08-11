package com.example.e_commercompose.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class AuthModleEntity
    (
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val token: String = "",
    val RefreshToken: String = ""
)