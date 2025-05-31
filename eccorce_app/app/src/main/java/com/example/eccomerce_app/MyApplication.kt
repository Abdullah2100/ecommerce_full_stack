package com.example.e_commercompose

import android.app.Application
import com.example.eccomerce_app.di.dataBaseModule
import com.example.e_commercompose.di.httpClientModule
import com.example.eccomerce_app.di.repositoryModel
import com.example.eccomerce_app.di.viewModelModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(
                dataBaseModule,
                httpClientModule,
                viewModelModel,
                repositoryModel,
              )
        }
    }
}