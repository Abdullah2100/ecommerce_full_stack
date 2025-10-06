package com.example.e_commerc_delivery_man.di

import android.util.Log
import com.example.e_commerc_delivery_man.data.Room.IAuthDao
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module



fun provideHttpClient(authDao:IAuthDao): HttpClient {
    return HttpClient(Android) {

        engine {
            connectTimeout = 60_000
        }

        install(WebSockets)

        install(HttpTimeout)

        install(Auth) {
            bearer {
                //  sendWithoutRequest { true }
//                loadTokens {
//                    BearerTokens(
//                        General.authData.value?.token?:"",
//                        General.authData.value?.refreshToken ?:""
//                    )
//                }

                refreshTokens {
//                    try {
//                        val refreshToken = client.
//                        post("${General.BASED_URL}/refreshToken/refresh") {
//                            url {
//                                parameters.append("tokenHolder", General.authData.value?.refreshToken ?: "")
//                            }
//                            markAsRefreshTokenRequest()
//                        }
//                        if(refreshToken.status== HttpStatusCode.OK){
//                            var result = refreshToken.body<AuthResultDto>()
//                            General.updateSavedToken(authDao, result)
//                            BearerTokens(
//                                accessToken = result.accessToken,
//                                refreshToken = result.refreshToken
//                            )
//                        }else if(refreshToken.status== HttpStatusCode.Unauthorized) {
//                            authDao.nukeTable()
//                            null;
//                        }else {
//                            null;
//                        }
//                    } catch (cause: Exception) {
//                        null
//                    }
null;

                    // Update saved tokens
                }
            }
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.v("Logger Ktor =>", message)
                }
            }
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }


    }
}

fun socketClient(): HubConnection? {
    try {
        var connectionUrl="http://10.0.2.2:5077"+"/bannerHub"
        return  HubConnectionBuilder
            .create(connectionUrl)
            .withTransport(com.microsoft.signalr.TransportEnum.LONG_POLLING)
            .build()
    }catch(e: Exception){
        return null;
    }
}

val httpClientModule = module {
    single { provideHttpClient(get()) }
    single { socketClient() }

}