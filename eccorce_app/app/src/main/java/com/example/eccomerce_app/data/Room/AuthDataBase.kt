package com.example.e_commercompose.data.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    AuthModleEntity::class,
    IsPassOnBoardingScreen::class,
    IsPassSetLocationScreen::class
                     ], version = 1, exportSchema = false)
abstract class AuthDataBase: RoomDatabase() {
    abstract fun authDao(): AuthDao
}