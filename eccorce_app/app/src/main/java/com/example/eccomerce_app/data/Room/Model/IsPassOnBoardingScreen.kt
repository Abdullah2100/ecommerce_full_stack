package com.example.eccomerce_app.data.Room.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IsPassOnBoardingScreen(
    @PrimaryKey
    var id: Int = 0,
    var condition: Boolean = false
)