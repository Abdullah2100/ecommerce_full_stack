package com.example.eccomerce_app.di

import com.example.e_commerc_delivery_man.data.repository.AuthRepository
import com.example.e_commerc_delivery_man.data.repository.HomeRepository
import org.koin.dsl.module

val repositoryModel = module {
    single { AuthRepository(get()) }
    single { HomeRepository(get()) }
}