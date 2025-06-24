package com.example.eccomerce_app.di

import com.example.e_commerc_delivery_man.viewModel.AuthViewModel
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel
import org.koin.dsl.module

val viewModelModel = module{
    single { AuthViewModel(get(),get()) }
    single { HomeViewModel(get(),get(),get()) }
}