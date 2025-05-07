package com.example.eccomerce_app.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.AuthDataBase
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.Util.General
import org.koin.dsl.module

fun provideDataBase(application: Context): AuthDataBase {

    return Room.databaseBuilder(
        application,
        AuthDataBase::class.java,
        "table_post"
    )
        .openHelperFactory(General.encryptionFactory(Secrets.getBaseUrl()))
        .fallbackToDestructiveMigration()
        .build()
}



val dataBaseModule= module {
    single { provideDataBase(application =get()) }
    single { get<AuthDataBase>().authDao() }
}