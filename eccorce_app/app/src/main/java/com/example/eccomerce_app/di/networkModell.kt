package com.example.eccomerce_app.di

import android.util.Log
import androidx.room.Room
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.AuthDataBase
import com.example.eccomerce_app.data.repository.AuthRepository
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.Util.General
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module



fun provideHttpClient(authDao:AuthDao): HttpClient {
    return HttpClient(Android) {

        engine {
            connectTimeout = 60_000
        }


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



val httpClientModule = module {
    single { provideHttpClient(get()) }

}