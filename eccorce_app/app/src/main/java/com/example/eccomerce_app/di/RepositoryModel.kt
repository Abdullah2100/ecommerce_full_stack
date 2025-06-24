package com.example.e_commercompose.di

import com.example.e_commercompose.data.repository.AuthRepository
import com.example.e_commercompose.data.repository.HomeRepository
import org.koin.dsl.module

val repositoryModel = module {
    single { AuthRepository(get()) }
    single { HomeRepository(get()) }
}