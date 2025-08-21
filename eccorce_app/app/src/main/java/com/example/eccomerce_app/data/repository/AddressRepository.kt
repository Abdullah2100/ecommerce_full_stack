package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.dto.AddressDto
import com.example.eccomerce_app.dto.CreateAddressDto
import com.example.eccomerce_app.dto.UpdateAddressDto
import com.example.eccomerce_app.util.Secrets
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class AddressRepository(val client: HttpClient) {
    suspend fun userAddNewAddress(locationData: CreateAddressDto): NetworkCallHandler {
        return try {
            val result = client.post(
                Secrets.getBaseUrl() + "/User/address"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
                setBody(
                    locationData
                )
                contentType(ContentType.Application.Json)

            }

            when (result.status) {
                HttpStatusCode.Created -> {
                    NetworkCallHandler.Successful(result.body<AddressDto?>())
                }

                else -> {
                    NetworkCallHandler.Error(
                        result.body<String>()
                    )
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

    suspend fun userUpdateAddress(locationData: UpdateAddressDto): NetworkCallHandler {
        return try {
            val result = client.put(
                Secrets.getBaseUrl() + "/User/address"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
                setBody(
                    locationData
                )
                contentType(ContentType.Application.Json)

            }

            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<AddressDto?>())
                }

                else -> {
                    NetworkCallHandler.Error(
                        result.body<String>()
                    )
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

    suspend fun deleteUserAddress(addressID: UUID): NetworkCallHandler {
        return try {
            val result = client.delete(
                Secrets.getBaseUrl() + "/User/address/${addressID}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }

            }

            when (result.status) {
                HttpStatusCode.NoContent -> {
                    NetworkCallHandler.Successful(true)
                }

                else -> {
                    NetworkCallHandler.Error(
                        result.body<String>()
                    )
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

    suspend fun setAddressAsCurrent(addressId: UUID): NetworkCallHandler {
        return try {
            val result = client.patch(
                Secrets.getBaseUrl() + "/User/address/active/${addressId}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.NoContent -> {
                    NetworkCallHandler.Successful(result.body<Boolean>())
                }

                else -> {
                    NetworkCallHandler.Error(
                        result.body<String>()
                    )
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

    suspend fun getStoreAddress(id: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Store/${id}/${pageNumber}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<AddressDto>())
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