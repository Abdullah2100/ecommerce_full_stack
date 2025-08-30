package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.dto.BannerDto
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.data.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.File
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class BannerRepository(val client: HttpClient) {

    suspend fun getBannerByStoreId(storeId: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Banner/${storeId}/${pageNumber}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.Companion.OK) {
                NetworkCallHandler.Successful(result.body<List<BannerDto>>())
            } else {
                NetworkCallHandler.Error(result.body<String>())
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun getRandomBanner(): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Banner"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.Companion.OK) {
                NetworkCallHandler.Successful(result.body<List<BannerDto>>())
            } else if (result.status == HttpStatusCode.Companion.NoContent) {
                NetworkCallHandler.Error("No Data Found")
            } else {
                NetworkCallHandler.Error(result.body<String>())
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun createBanner(endDate: String, image: File): NetworkCallHandler {
        return try {
            val result = client.post(
                Secrets.getBaseUrl() + "/Banner"
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
                            append("end_at", endDate)
                            append(
                                key = "image", // Must match backend expectation
                                value = image.readBytes(),
                                headers = Headers.Companion.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/${image.extension}"
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${image.name}"
                                    )
                                }
                            )

                        }
                    )
                )
            }
            if (result.status == HttpStatusCode.Companion.Created) {
                NetworkCallHandler.Successful(result.body<BannerDto>())
            } else {
                NetworkCallHandler.Error(result.body())
            }
        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun deleteBanner(bannerId: UUID): NetworkCallHandler {
        return try {
            val result = client.delete(
                Secrets.getBaseUrl() + "/Banner/${bannerId}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }
            if (result.status == HttpStatusCode.Companion.NoContent) {
                NetworkCallHandler.Successful("deleted Seccessfuly")
            } else {
                NetworkCallHandler.Error(result.body())
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