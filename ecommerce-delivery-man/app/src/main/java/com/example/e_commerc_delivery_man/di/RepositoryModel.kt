package com.example.e_commerc_delivery_man.di

import com.example.e_commerc_delivery_man.data.repository.AuthRepository
import com.example.e_commerc_delivery_man.data.repository.OrderRepository
import com.example.e_commerc_delivery_man.data.repository.UserRepository
import com.example.eccomerce_app.data.repository.MapRepository
import org.koin.dsl.module

val repositoryModel = module {
    single { AuthRepository(get()) }
    single { OrderRepository(get()) }
    single { UserRepository(get()) }
    single { MapRepository(get()) }
}