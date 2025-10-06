package com.example.e_commerc_delivery_man.data.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AuthModleEntity::class, IsSetLocation::class], version = 1, exportSchema = false
)
abstract class AuthDataBase : RoomDatabase() {
    abstract fun authDao(): IAuthDao
    abstract fun locationDao(): ILocationDao
}