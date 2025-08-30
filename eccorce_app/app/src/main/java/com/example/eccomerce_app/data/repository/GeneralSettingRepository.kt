package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.dto.GeneralSettingDto
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.data.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.UnknownHostException

class GeneralSettingRepository(val client: HttpClient)   {

     suspend fun getGeneral(pageNumber: Int): NetworkCallHandler {
        return try {
            val result = client.get(
                Secrets.getBaseUrl() + "/General/all/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }
            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<List<GeneralSettingDto>>())
                }

                HttpStatusCode.Companion.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")

                }

                else -> {
                    NetworkCallHandler.Error(result.body())
                }
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

}