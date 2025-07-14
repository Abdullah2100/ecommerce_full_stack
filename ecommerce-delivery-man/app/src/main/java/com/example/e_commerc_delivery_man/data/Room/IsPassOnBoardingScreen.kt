package com.example.e_commerc_delivery_man.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IsPassOnBoardingScreen(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val default: Boolean=false
)
