package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.dto.StoreDto
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.data.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.File
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class StoreRepository(val client: HttpClient)   {

     suspend fun createStore(
        name: String,
        wallpaperImage: File,
        smallImage: File,
        longitude: Double,
        latitude: Double
    ): NetworkCallHandler {
        return try {
            val result = client.post(
                Secrets.getBaseUrl() + "/Store"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("Name", name)
                            append("Longitude", latitude)
                            append("Latitude", longitude)
                            append(
                                key = "WallpaperImage", // Must match backend expectation
                                value = wallpaperImage.readBytes(),
                                headers = Headers.Companion.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/${wallpaperImage.extension}"
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${wallpaperImage.name}"
                                    )
                                }
                            )
                            append(
                                key = "SmallImage", // Must match backend expectation
                                value = smallImage.readBytes(),
                                headers = Headers.Companion.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/${smallImage.extension}"
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${smallImage.name}"
                                    )
                                }
                            )

                        }
                    )
                )
            }
            when (result.status) {
                HttpStatusCode.Companion.Created -> {
                    NetworkCallHandler.Successful(result.body<StoreDto>())
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

     suspend fun updateStore(
        name: String,
        wallpaperImage: File?,
        smallImage: File?,
        longitude: Double?,
        latitude: Double?
    ): NetworkCallHandler {
        return try {
            val result = client.put(
                Secrets.getBaseUrl() + "/Store"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            if (name.trim().length > 0)
                                append("name", name)
                            if (latitude != null)
                                append("longitude", latitude)
                            if (longitude != null)
                                append("latitude", longitude)
                            if (wallpaperImage != null)
                                append(
                                    key = "wallpaper_image", // Must match backend expectation
                                    value = wallpaperImage.readBytes(),
                                    headers = Headers.Companion.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${wallpaperImage.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${wallpaperImage.name}"
                                        )
                                    }
                                )
                            if (smallImage != null)
                                append(
                                    key = "small_image", // Must match backend expectation
                                    value = smallImage.readBytes(),
                                    headers = Headers.Companion.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${smallImage.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${smallImage.name}"
                                        )
                                    }
                                )

                        }
                    )
                )
            }
            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<StoreDto>())
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

     suspend fun getStoreById(id: UUID): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Store/${id}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<StoreDto>())
                }

                else -> {
                    NetworkCallHandler.Error(result.body<String>())
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