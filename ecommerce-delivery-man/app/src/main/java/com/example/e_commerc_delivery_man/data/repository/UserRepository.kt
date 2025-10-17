package com.example.e_commerc_delivery_man.data.repository

import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.dto.DeliveryDto
import com.example.e_commerc_delivery_man.model.Address
import com.example.e_commerc_delivery_man.model.Delivery
import com.example.e_commerc_delivery_man.model.UpdateMyInfo
import com.example.e_commerc_delivery_man.util.Secrets
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.InternalAPI
import java.io.File
import java.io.IOException
import java.net.UnknownHostException

class UserRepository(val client: HttpClient) {
    suspend fun getMyInfo(): NetworkCallHandler {
        try {
            val result = client.get(
                Secrets.getBaseUrl() + "/Delivery"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }
            return if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<DeliveryDto>())
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


    suspend fun updateDeliveryState(status: Boolean): NetworkCallHandler {
        try {
            val result = client.patch(
                Secrets.getBaseUrl() + "/Delivery/${status}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }
            return if (result.status == HttpStatusCode.NoContent) {
                NetworkCallHandler.Successful(true)
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

    @OptIn(InternalAPI::class)
    suspend fun updateDeliveryInfo(
        address: Address? = null,
        thumbnail: File? = null,
        userInfo: UpdateMyInfo? = null
    ): NetworkCallHandler {
        try {
            val result = client.put(
                Secrets.getBaseUrl() + "/Delivery"
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
                            if (thumbnail != null)
                                append(
                                    key = "Thumbnail", // Must match backend expectation
                                    value = thumbnail.readBytes(),
                                    headers = Headers.Companion.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${thumbnail.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${thumbnail.name}"
                                        )
                                    }
                                )

                            if (userInfo?.thumbnail != null)
                                append(
                                    key = "UserThumbnail", // Must match backend expectation
                                    value = thumbnail!!.readBytes(),
                                    headers = Headers.Companion.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${thumbnail.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${thumbnail.name}"
                                        )
                                    }
                                )
                            if (address?.longitude != null)
                                append("Longitude", address.longitude)

                            if (address?.latitude != null)
                                append("Latitude", address.latitude)

                            if (userInfo?.name != null)
                                append("Name", userInfo.name)
                            if (userInfo?.phone != null)
                                append("Phone", userInfo.phone)
                            if (userInfo?.oldPassword != null && userInfo?.newPassword != null) {
                                append("Password", userInfo.oldPassword)
                                append("NewPassword", userInfo.newPassword)
                            }
                        }
                    )
                )
            }
            return if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<DeliveryDto>())
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