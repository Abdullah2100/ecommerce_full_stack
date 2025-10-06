package com.example.e_commerc_delivery_man.data.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IAuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAuthData(authData : AuthModleEntity)

    @Query("SELECT * FROM DeliveryAuthT")
    suspend  fun getAuthData(): AuthModleEntity?

    @Query("DELETE FROM DeliveryAuthT ")
   suspend fun nukeTable()


    @Query("SELECT * FROM DeliveryAuthT WHERE id = 0")
     fun   readChunksLive(): Flow<AuthModleEntity?>;




}