package com.example.eccomerce_app.di

import com.example.eccomerce_app.util.Secrets
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.TransportEnum
import org.koin.core.qualifier.named
import org.koin.dsl.module


fun bannerEvent(): HubConnection? {
    try {
        val connectionUrl=Secrets.getBaseUrl().replace("/api","")+"/bannerHub"
        return  HubConnectionBuilder
            .create(connectionUrl)
            .withTransport(TransportEnum.LONG_POLLING)
            .build()
    }catch(e: Exception){
        return null
    }
}

fun orderEvent(): HubConnection? {
    try {
        val connectionUrl=Secrets.getBaseUrl().replace("/api","")+"/orderHub"
        return  HubConnectionBuilder
            .create(connectionUrl)
            .withTransport(TransportEnum.LONG_POLLING)
            .build()
    }catch(e: Exception){
        return null
    }
}
fun orderItemEvent(): HubConnection? {
    try {
        val connectionUrl=Secrets.getBaseUrl().replace("/api","")+"/orderItemHub"
        return  HubConnectionBuilder
            .create(connectionUrl)
            .withTransport(TransportEnum.LONG_POLLING)
            .build()
    }catch(e: Exception){
        return null
    }
}

fun storeEvent(): HubConnection? {
    try {
        val connectionUrl=Secrets.getBaseUrl().replace("/api","")+"/storeHub"
        return  HubConnectionBuilder
            .create(connectionUrl)
            .withTransport(TransportEnum.LONG_POLLING)
            .build()
    }catch(e: Exception){
        return null
    }
}

val webSocketClientModule = module {
    single { provideHttpClient(get()) }
    single(named("bannerHub")) { bannerEvent() }
    single (named("orderHub")){ orderEvent() }
    single (named("orderItemHub")){ orderItemEvent() }
    single (named("storeHub")){ storeEvent() }
}