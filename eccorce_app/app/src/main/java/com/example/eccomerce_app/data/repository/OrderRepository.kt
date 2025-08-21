package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.dto.CreateOrderDto
import com.example.eccomerce_app.dto.OrderDto
import com.example.eccomerce_app.util.Secrets
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class OrderRepository(val client: HttpClient) {

    suspend fun submitOrder(cartData: CreateOrderDto): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Order"
            val result = client.post(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
                contentType(ContentType.Application.Json)
                setBody(cartData)

            }

            when (result.status) {
                HttpStatusCode.Created -> {

                    NetworkCallHandler.Successful(result.body<OrderDto>())

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

    suspend fun getMyOrders(pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Order/me/${pageNumber}"
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

                    NetworkCallHandler.Successful(result.body<List<OrderDto>>())

                }

                HttpStatusCode.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")
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

    suspend fun deleteOrder(orderId: UUID): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Order/${orderId}"
            val result = client.delete(fullUrl) {
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