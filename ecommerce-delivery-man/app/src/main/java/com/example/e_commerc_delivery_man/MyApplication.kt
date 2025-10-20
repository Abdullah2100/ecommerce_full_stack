package com.example.e_commerc_delivery_man

import android.app.Application
import com.example.eccomerce_app.di.dataBaseModule
import com.example.e_commerc_delivery_man.di.httpClientModule
import com.example.e_commerc_delivery_man.di.repositoryModel
import com.example.e_commerc_delivery_man.di.viewModelModel
import com.example.eccomerce_app.di.webSocketClientModule
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
                webSocketClientModule
              )
        }
    }
}