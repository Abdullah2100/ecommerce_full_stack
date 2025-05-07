package com.example.eccomerce_app.di

import com.example.eccomerce_app.data.repository.AuthRepository
import org.koin.dsl.module

val repositoryModel = module {
    single { AuthRepository(get()) }
}