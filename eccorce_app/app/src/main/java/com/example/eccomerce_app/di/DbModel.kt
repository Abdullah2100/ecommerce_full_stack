package com.example.e_commercompose.di

import android.content.Context
import androidx.room.Room
import com.example.e_commercompose.data.Room.AuthDataBase
import com.example.e_commercompose.util.Secrets
import com.example.e_commercompose.Util.General
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