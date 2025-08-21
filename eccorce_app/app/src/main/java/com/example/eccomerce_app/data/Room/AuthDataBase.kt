package com.example.eccomerce_app.data.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.e_commercompose.data.Room.AuthModleEntity
import com.example.e_commercompose.data.Room.IsPassOnBoardingScreen
import com.example.e_commercompose.data.Room.IsPassSetLocationScreen

@Database(entities = [
    AuthModleEntity::class,
    IsPassOnBoardingScreen::class,
    IsPassSetLocationScreen::class
                     ], version = 1, exportSchema = false)
abstract class AuthDataBase: RoomDatabase() {
    abstract fun authDao(): AuthDao
}