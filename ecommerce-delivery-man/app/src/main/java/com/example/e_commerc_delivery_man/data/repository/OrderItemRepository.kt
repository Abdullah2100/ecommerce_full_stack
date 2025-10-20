package com.example.e_commerc_delivery_man.data.repository

import com.example.e_commerc_delivery_man.util.General
import com.example.e_commerc_delivery_man.util.Secrets
import com.example.eccomerce_app.dto.response.OrderDto
import com.example.eccomerce_app.dto.response.UpdateOrderStatus
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class OrderItemRepository(val client: HttpClient) {

    suspend fun updateOrderItemStatus(orderUpdate: UpdateOrderStatus): NetworkCallHandler {

        try {
            val result = client.put(
                Secrets.getBaseUrl() + "/OrderItems/statsu"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(orderUpdate)
                contentType(ContentType.Application.Json)

            }
            return if (result.status == HttpStatusCode.OK) {
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


}