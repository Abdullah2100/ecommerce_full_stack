package com.example.eccomerce_app.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.eccomerce_app.data.Room.AuthDataBase
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.util.General
import org.koin.dsl.module

fun provideDataBase(application: Context): AuthDataBase {
    return Room.databaseBuilder(
        application,
        AuthDataBase::class.java,
        "table_post"
    )
        .openHelperFactory(General.encryptionFactory(Secrets.getBaseUrl()))
        .build()
}


val dataBaseModule = module {
    single { provideDataBase(application = get()) }
    single { get<AuthDataBase>().authDao() }
}