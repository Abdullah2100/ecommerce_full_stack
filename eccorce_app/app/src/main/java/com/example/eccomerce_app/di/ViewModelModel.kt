package com.example.eccomerce_app.di

import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.viewModel.HomeViewModel
import org.koin.dsl.module

val viewModelModel = module{
    single { AuthViewModel(get(),get()) }
    single { HomeViewModel(get(),get()) }
}