package com.example.e_commerc_delivery_man.data.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ILocationDao {
    @Query("SELECT COUNT(*)>0 FROM location WHERE condition=true")
    suspend fun isPassLocationScreen(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savedPassLocation(isSetLocation: IsSetLocation)


}