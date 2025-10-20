package com.example.e_commerc_delivery_man.di

import com.example.e_commerc_delivery_man.viewModel.AuthViewModel
import com.example.e_commerc_delivery_man.viewModel.OrderViewModel
import com.example.e_commerc_delivery_man.viewModel.UserViewModel
import com.example.eccomerce_app.viewModel.MapViewModel
import org.koin.dsl.module
import org.koin.core.qualifier.named

val viewModelModel = module {
    single { AuthViewModel(get(), get(), get()) }
    single {
        OrderViewModel(
            get(),
            get(),
            get(named("orderHub")),
            get(named("orderItemHub"))
        )
    }
    single { UserViewModel(get(), get()) }
    single { MapViewModel(get()) }
}