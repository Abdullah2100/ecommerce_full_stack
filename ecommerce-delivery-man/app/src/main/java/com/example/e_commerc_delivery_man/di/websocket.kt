package com.example.eccomerce_app.di

import com.example.e_commerc_delivery_man.util.Secrets
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.TransportEnum
import org.koin.core.qualifier.named
import org.koin.dsl.module


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



val webSocketClientModule = module {
    single (named("orderHub")){ orderEvent() }
    single (named("orderItemHub")){ orderItemEvent() }
}