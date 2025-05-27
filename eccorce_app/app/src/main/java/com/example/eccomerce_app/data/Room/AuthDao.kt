package com.example.eccomerce_app.data.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAuthData(authData : AuthModleEntity)

    @Query("SELECT * FROM AuthModleEntity")
    suspend  fun getAuthData(): AuthModleEntity?

    @Query("DELETE FROM AuthModleEntity ")
   suspend fun nukeTable()


    @Query("SELECT * FROM AuthModleEntity WHERE id = 0")
     fun   readChunksLive(): Flow<AuthModleEntity?>;

    @Query("SELECT count(*)>0 FROM ispassonboardingscreen WHERE  id = 0")
    fun   isPassOnBoarding(): Boolean;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveIsPassingOnBoarding(value : IsPassOnBoardingScreen)


    @Query("SELECT count(*)>0 FROM location WHERE  id = 0")
   suspend fun   isPassLocationScreen(): Boolean;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePassingLocation(authData : IsPassSetLocationScreen)

    @Query("SELECT * FROM location WHERE id =0")
    suspend fun getSavedLocation(): IsPassSetLocationScreen?

    @Query("DELETE FROM location ")
    suspend fun nukePassLocationTable()
}