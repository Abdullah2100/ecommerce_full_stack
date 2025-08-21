package com.example.e_commercompose.data.repository

import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.dto.VarientDto
import com.example.eccomerce_app.util.Secrets
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.UnknownHostException

class VariantRepository(val client: HttpClient) {
    suspend fun getVariant(pageNumber: Int = 1): NetworkCallHandler {
        return try {
            val result = client.get(
                Secrets.getBaseUrl() + "/Varient/all/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
            }
            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<List<VarientDto>>())
                }

                HttpStatusCode.NoContent -> {
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