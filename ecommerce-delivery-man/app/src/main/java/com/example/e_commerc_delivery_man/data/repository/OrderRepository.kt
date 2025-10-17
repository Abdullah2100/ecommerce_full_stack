package com.example.e_commerc_delivery_man.data.repository

import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.util.Secrets
import com.example.eccomerce_app.dto.response.OrderItemResponseDto
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
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class OrderRepository(val client: HttpClient) {


    //order

    suspend fun getOrdersNoSubmitted(pageNumber: Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Order/delivery/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.OK) {

                NetworkCallHandler.Successful(result.body<List<OrderItemResponseDto>>())

            } else if (result.status == HttpStatusCode.NoContent) {
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



    suspend fun updateOrderStatus(orderUpdate: UpdateOrderStatus): NetworkCallHandler
    {

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

            }
            return if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(true)
            } else {
                NetworkCallHandler.Error(result.body())
            }
        }
        catch (e: UnknownHostException)
        {

            return NetworkCallHandler.Error(e.message)

        }
        catch (e: IOException)
        {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception)
        {

            return NetworkCallHandler.Error(e.message)
        }
    }


    //orders
    suspend fun getOrders(pageNumber: Int): NetworkCallHandler {
        try {
            val result = client.get(
                Secrets.getBaseUrl() + "/Delivery/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }
            return if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<OrderDto>>())
            } else {
                NetworkCallHandler.Error(result.body())
            }
        }
        catch (e: UnknownHostException)
        {

            return NetworkCallHandler.Error(e.message)

        }
        catch (e: IOException)
        {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception)
        {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun getOrdersBelongToMe(pageNumber: Int): NetworkCallHandler {
        try {
            val result = client.get(
                Secrets.getBaseUrl() + "/Delivery/me/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }
            return if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<OrderDto>>())
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

    suspend fun takeOrder(orderId: UUID): NetworkCallHandler {
        try {
            val result = client.patch(
                Secrets.getBaseUrl() + "/Delivery/${orderId}"
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


    suspend fun cancelOrder(orderId: UUID): NetworkCallHandler {
        try {
            val result = client.delete(
                Secrets.getBaseUrl() + "/Delivery/${orderId}"
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

}