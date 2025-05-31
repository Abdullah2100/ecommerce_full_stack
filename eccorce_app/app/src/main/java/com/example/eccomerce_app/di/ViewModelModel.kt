package com.example.eccomerce_app.di

import com.example.e_commercompose.viewModel.AuthViewModel
import com.example.e_commercompose.viewModel.HomeViewModel
import org.koin.dsl.module

val viewModelModel = module{
    single { AuthViewModel(get(),get()) }
    single { HomeViewModel(get(),get(),get()) }
}