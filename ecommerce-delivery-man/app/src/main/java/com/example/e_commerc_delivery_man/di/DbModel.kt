package com.example.eccomerce_app.di

import android.content.Context
import androidx.room.Room
import com.example.e_commerc_delivery_man.data.Room.AuthDataBase
import com.example.e_commerc_delivery_man.util.Secrets
import com.example.e_commerc_delivery_man.util.General
import org.koin.dsl.module

fun provideDataBase(application: Context): AuthDataBase {

    return Room.databaseBuilder(
        application,
        AuthDataBase::class.java,
        "table_post"
    )
        .openHelperFactory(General.encryptionFactory(Secrets.getBaseUrl()))
        .fallbackToDestructiveMigration(false)
        .build()
}



val dataBaseModule= module {
    single { provideDataBase(application =get()) }
    single { get<AuthDataBase>().authDao() }
    single { get<AuthDataBase>().locationDao() }
}