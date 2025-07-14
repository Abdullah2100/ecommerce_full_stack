package com.example.e_commerc_delivery_man.data.repository

import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.dto.request.AddressRequestDto
import com.example.e_commerc_delivery_man.dto.request.AddressRequestUpdateDto
import com.example.e_commerc_delivery_man.dto.request.CartRequestDto
import com.example.e_commerc_delivery_man.dto.request.OrderRequestItemsDto
import com.example.e_commerc_delivery_man.dto.request.SubCategoryRequestDto
import com.example.e_commerc_delivery_man.dto.request.SubCategoryUpdateDto
import com.example.e_commerc_delivery_man.dto.response.AddressResponseDto
import com.example.e_commerc_delivery_man.dto.response.BannerResponseDto
import com.example.e_commerc_delivery_man.dto.response.CategoryReponseDto
import com.example.e_commerc_delivery_man.dto.response.ProductResponseDto
import com.example.e_commerc_delivery_man.dto.response.StoreResposeDto
import com.example.e_commerc_delivery_man.dto.response.SubCategoryResponseDto
import com.example.e_commerc_delivery_man.dto.response.UserDto
import com.example.e_commerc_delivery_man.dto.response.VarientResponseDto
import com.example.e_commerc_delivery_man.model.MyInfoUpdate
import com.example.e_commerc_delivery_man.model.ProductVarientSelection
import com.example.e_commerc_delivery_man.util.Secrets
import com.example.eccomerce_app.dto.request.OrderItemUpdateStatusDto
import com.example.eccomerce_app.dto.response.GeneralSettingResponseDto
import com.example.eccomerce_app.dto.response.OrderItemResponseDto
import com.example.eccomerce_app.dto.response.OrderResponseDto
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.File
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class HomeRepository(val client: HttpClient) {




    //order
    suspend fun submitOrder(cartData: CartRequestDto): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Order";
            val result = client.post(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                contentType(ContentType.Application.Json)
                setBody(cartData)

            }

            if (result.status == HttpStatusCode.Created) {

                NetworkCallHandler.Successful(result.body<OrderResponseDto>())

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
    suspend fun getMyOrders(pageNumber:Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Order/me/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.OK) {

                NetworkCallHandler.Successful(result.body<List<OrderResponseDto>>())

            } else if(result.status == HttpStatusCode.NoContent){
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
    suspend fun deleteOrder(order_Id: UUID): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Order/${order_Id}";
            val result = client.delete(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.NoContent) {

                NetworkCallHandler.Successful(true)

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

    suspend fun updateOrderItemStatus(id: UUID, status: Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Order/orderItem/statsu";
            val result = client.put(full_url) {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(OrderItemUpdateStatusDto(id,status))
            }

            if (result.status == HttpStatusCode.NoContent) {
                NetworkCallHandler.Successful(true)
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


    suspend fun getOrdersNoSubmitted(pageNumber:Int): NetworkCallHandler {
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

            } else if(result.status == HttpStatusCode.NoContent){
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



    //user
    suspend fun userAddNewAddress(locationData: AddressRequestDto): NetworkCallHandler {
        return try {
            var result = client.post(
                Secrets.getBaseUrl() + "/User/address"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(
                    locationData
                )
                contentType(ContentType.Application.Json)

            }

            if (result.status == HttpStatusCode.Created) {
                NetworkCallHandler.Successful(result.body<AddressResponseDto?>())
            } else {
                NetworkCallHandler.Error(
                    result.body<String>()
                )
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun userUpdateAddress(locationData: AddressRequestUpdateDto): NetworkCallHandler {
        return try {
            var result = client.put(
                Secrets.getBaseUrl() + "/User/address"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(
                    locationData
                )
                contentType(ContentType.Application.Json)

            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<AddressResponseDto?>())
            } else {
                NetworkCallHandler.Error(
                    result.body<String>()
                )
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }


    suspend fun getMyInfo(): NetworkCallHandler {
        return try {
            var result = client.get(
                Secrets.getBaseUrl() + "/User"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }
            if (result.status == HttpStatusCode.OK) {
                return NetworkCallHandler.Successful(result.body<UserDto>())
            } else {
                return NetworkCallHandler.Error(result.body())
            }
        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun UpdateMyInfo(data: MyInfoUpdate): NetworkCallHandler {
        return try {
            var result = client.put(
                Secrets.getBaseUrl() + "/User"
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
                            if (!data.name.isNullOrEmpty())
                                append("name", data.name!!)

                            if (!data.oldPassword.isNullOrEmpty())
                                append("name", data.oldPassword!!)


                            if (!data.phone.isNullOrEmpty())
                                append("phone", data.phone!!)


                            if (!data.newPassword.isNullOrEmpty())
                                append("name", data.newPassword!!)

                            if (data.thumbnail != null)
                                append(
                                    key = "thumbnail", // Must match backend expectation
                                    value = data.thumbnail!!.readBytes(),
                                    headers = Headers.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${data.thumbnail!!.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${data.thumbnail!!.name}"
                                        )
                                    }
                                )

                        }
                    )
                )
            }
            if (result.status == HttpStatusCode.OK) {
                return NetworkCallHandler.Successful(result.body<UserDto>())
            } else {
                return NetworkCallHandler.Error(result.body())
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