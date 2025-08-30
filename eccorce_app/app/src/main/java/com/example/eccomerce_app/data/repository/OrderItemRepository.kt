package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.dto.OrderItemDto
import com.example.eccomerce_app.dto.UpdateOrderItemStatusDto
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.data.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class OrderItemRepository(val client: HttpClient)  {
     suspend fun getMyOrderItemForStoreId( pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Order/orderItems/${pageNumber}"
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

                    NetworkCallHandler.Successful(result.body<List<OrderItemDto>>())

                }

                HttpStatusCode.Companion.NoContent -> {
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

     suspend fun updateOrderItemStatus(id: UUID, status: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Order/orderItems/statsu"
            val result = client.put(fullUrl) {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(UpdateOrderItemStatusDto(id, status))
            }

            when (result.status) {
                HttpStatusCode.Companion.NoContent -> {
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