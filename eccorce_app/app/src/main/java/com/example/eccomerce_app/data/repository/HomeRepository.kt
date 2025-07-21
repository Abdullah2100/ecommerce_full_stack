package com.example.e_commercompose.data.repository

import com.example.e_commercompose.Util.General
import com.example.e_commercompose.dto.request.AddressRequestDto
import com.example.e_commercompose.dto.request.AddressRequestUpdateDto
import com.example.e_commercompose.dto.request.CartRequestDto
import com.example.e_commercompose.dto.request.OrderRequestItemsDto
import com.example.e_commercompose.dto.request.SubCategoryRequestDto
import com.example.e_commercompose.dto.request.SubCategoryUpdateDto
import com.example.e_commercompose.dto.response.AddressResponseDto
import com.example.e_commercompose.dto.response.BannerResponseDto
import com.example.e_commercompose.dto.response.CategoryReponseDto
import com.example.e_commercompose.dto.response.ProductResponseDto
import com.example.e_commercompose.dto.response.StoreResposeDto
import com.example.e_commercompose.dto.response.SubCategoryResponseDto
import com.example.e_commercompose.dto.response.UserDto
import com.example.e_commercompose.dto.response.VarientResponseDto
import com.example.e_commercompose.model.MyInfoUpdate
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.util.Secrets
import com.example.e_commercompose.dto.request.OrderItemUpdateStatusDto
import com.example.e_commercompose.dto.response.GeneralSettingResponseDto
import com.example.e_commercompose.dto.response.OrderItemResponseDto
import com.example.e_commercompose.dto.response.OrderResponseDto
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

    /*
    suspend fun getUserAddress(): NetworkCallHandler {
        return try {
            var result = client.get(
                Secrets.getBaseUrl() + "/User/address"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<AddressResponseDto>>())
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
*/

    //Banner
    suspend fun getBannerByStoreId(store_id: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Banner/${store_id}/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<BannerResponseDto>>())
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
            val full_url = Secrets.getBaseUrl() + "/Banner";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<BannerResponseDto>>())
            } else if(result.status== HttpStatusCode.NoContent) {
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


    suspend fun createBanner(endDate: String, image: File, ): NetworkCallHandler {
        return try {
            var result = client.post(
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
                                headers = Headers.build {
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
            if (result.status == HttpStatusCode.Created) {
                return NetworkCallHandler.Successful(result.body<BannerResponseDto>())
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

    suspend fun deleteBanner(banner_id: UUID): NetworkCallHandler {
        return try {
            var result = client.delete(
                Secrets.getBaseUrl() + "/Banner/${banner_id}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }

            }
            if (result.status == HttpStatusCode.NoContent) {
                return NetworkCallHandler.Successful("deleted Seccessfuly")
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



    //category
    suspend fun getCategory(pageNumber: Int = 1): NetworkCallHandler {
        return try {
            var result = client.get(
                Secrets.getBaseUrl() + "/Category/all/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }
            if (result.status == HttpStatusCode.OK) {
                return NetworkCallHandler.Successful(result.body<List<CategoryReponseDto>>())
            } else if (result.status == HttpStatusCode.NoContent) {
                return NetworkCallHandler.Error("No Data Found")

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

    //general
    suspend fun getGeneral(pageNumber: Int = 1): NetworkCallHandler {
        return try {
            var result = client.get(
                Secrets.getBaseUrl() + "/General/all/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }
            if (result.status == HttpStatusCode.OK) {
                return NetworkCallHandler.Successful(result.body<List<GeneralSettingResponseDto>>())
            } else if (result.status == HttpStatusCode.NoContent) {
                return NetworkCallHandler.Error("No Data Found")

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

    suspend fun getMyOrderItemForStoreId(store_id: UUID,pageNumber:Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Order/orderItem/${store_id}/${pageNumber}";
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


    //product

    suspend fun getProduct(pageNumber: Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Product/all/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<ProductResponseDto>>())
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

    suspend fun getProductByCategoryId(category_id: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Product/category/${category_id}/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<ProductResponseDto>>())
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

    suspend fun getProduct(store_id: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Product/${store_id}/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<ProductResponseDto>>())
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

    suspend fun getProduct(store_id: UUID, subCatgory: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val full_url =
                Secrets.getBaseUrl() + "/Product/${store_id}/${subCatgory}/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<ProductResponseDto>>())
            }else if(result.status == HttpStatusCode.NoContent){
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


    suspend fun createProduct(
        name: String,
        description: String,
        thmbnail: File,
        subcategory_id: UUID,
        store_id: UUID,
        price: Double,
        productVarients: List<ProductVarientSelection>,
        images: List<File>
    ): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Product";
            val result = client.post(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("name", name)
                            append("description", description)
                            append(
                                key = "thmbnail", // Must match backend expectation
                                value = thmbnail.readBytes(),
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/${thmbnail.extension}"
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${thmbnail.name}"
                                    )
                                }
                            )

                            append("subcategoryId", subcategory_id.toString())
                            append("storeId", store_id.toString())
                            append("price", price)
                            if (productVarients.isNotEmpty())
                                productVarients.forEachIndexed { it, value ->
                                    append("productVarients[${it}].name", value.name)
                                    append("productVarients[${it}].precentage", value.precentage!!)
                                    append(
                                        "productVarients[${it}].varientId",
                                        value.varient_id.toString()
                                    )
                                }
                            images.forEachIndexed { it, value ->
                                append(
                                    key = "images", // Must match backend expectation
                                    value = value.readBytes(),
                                    headers = Headers.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${value.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${value.name}"
                                        )
                                    }
                                )
                            }


                        }
                    )
                )
            }

            if (result.status == HttpStatusCode.Created) {
                NetworkCallHandler.Successful(result.body<ProductResponseDto>())
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

    suspend fun updateProduct(
        id: UUID,
        name: String?,
        description: String?,
        thmbnail: File?,
        subcategory_id: UUID?,
        store_id: UUID,
        price: Double?,
        productVarients: List<ProductVarientSelection>?,
        images: List<File>?,
        deletedProductVarients: List<ProductVarientSelection>?,
        deletedimages: List<String>?


    ): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Product";
            val result = client.put(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("id", id.toString())
                            if (name != null)
                                append("name", name)
                            if (description != null)
                                append("description", description)
                            if (thmbnail != null)
                                append(
                                    key = "thmbnail", // Must match backend expectation
                                    value = thmbnail.readBytes(),
                                    headers = Headers.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${thmbnail.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${thmbnail.name}"
                                        )
                                    }
                                )
                            if (subcategory_id != null)
                                append("subcategoryId", subcategory_id.toString())
                            append("storeId", store_id.toString())

                            if (price != null)
                                append("price", price)

                            if (!productVarients.isNullOrEmpty())
                                productVarients.forEachIndexed { it, value ->
                                    append("productVarients[${it}].name", value.name)
                                    append("productVarients[${it}].precentage", value.precentage!!)
                                    append(
                                        "productVarients[${it}].varientId",
                                        value.varient_id.toString()
                                    )
                                }
                            if (!deletedProductVarients.isNullOrEmpty())
                                deletedProductVarients.forEachIndexed { it, value ->
                                    append("deletedProductVarients[${it}].name", value.name)
                                    append(
                                        "deletedProductVarients[${it}].precentage",
                                        value.precentage!!
                                    )
                                    append(
                                        "deletedProductVarients[${it}].varientId",
                                        value.varient_id.toString()
                                    )

                                }

                            if (!deletedimages.isNullOrEmpty())
                                deletedimages.forEachIndexed { it, value ->
                                    val startIndex = "staticFiles"
                                    val indexAt = value.indexOf("staticFiles")
                                    append(
                                        "deletedimages[${it}]", value.substring(
                                            indexAt + startIndex.length,
                                            value.length
                                        )
                                    )
                                }


                            if (!images.isNullOrEmpty())
                                images.forEachIndexed { it, value ->
                                    append(
                                        key = "images", // Must match backend expectation
                                        value = value.readBytes(),
                                        headers = Headers.build {
                                            append(
                                                HttpHeaders.ContentType,
                                                "image/${value.extension}"
                                            )
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=${value.name}"
                                            )
                                        }
                                    )
                                }


                        }
                    )
                )
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<ProductResponseDto>())
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

    suspend fun deleteProduct(store_id: UUID, product_id: UUID): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Product/${store_id}/${product_id}";
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



    //store
    suspend fun createStore(
        name: String,
        wallpaper_image: File,
        small_image: File,
        longitude: Double,
        latitude: Double
    ): NetworkCallHandler {
        return try {
            var result = client.post(
                Secrets.getBaseUrl() + "/Store/new"
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
                            append("name", name)
                            append("longitude", latitude)
                            append("latitude", longitude)
                            append(
                                key = "wallpaperImage", // Must match backend expectation
                                value = wallpaper_image.readBytes(),
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/${wallpaper_image.extension}"
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${wallpaper_image.name}"
                                    )
                                }
                            )
                            append(
                                key = "smallImage", // Must match backend expectation
                                value = small_image.readBytes(),
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/${small_image.extension}"
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${small_image.name}"
                                    )
                                }
                            )

                        }
                    )
                )
            }
            if (result.status == HttpStatusCode.Created) {
                return NetworkCallHandler.Successful(result.body<StoreResposeDto>())
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

    suspend fun updateStore(
        name: String,
        wallpaper_image: File?,
        small_image: File?,
        longitude: Double,
        latitude: Double
    ): NetworkCallHandler {
        return try {
            var result = client.put(
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
                            if (latitude != 0.0)
                                append("longitude", latitude)
                            if (longitude != 0.0)
                                append("latitude", longitude)
                            if (wallpaper_image != null)
                                append(
                                    key = "wallpaper_image", // Must match backend expectation
                                    value = wallpaper_image.readBytes(),
                                    headers = Headers.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${wallpaper_image.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${wallpaper_image.name}"
                                        )
                                    }
                                )
                            if (small_image != null)
                                append(
                                    key = "small_image", // Must match backend expectation
                                    value = small_image.readBytes(),
                                    headers = Headers.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${small_image.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${small_image.name}"
                                        )
                                    }
                                )

                        }
                    )
                )
            }
            if (result.status == HttpStatusCode.OK) {
                return NetworkCallHandler.Successful(result.body<StoreResposeDto>())
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

    suspend fun getStoreById(id: UUID): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Store/${id}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<StoreResposeDto>())
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

    suspend fun getStoreAddress(id: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/Store/${id}/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<AddressResponseDto>())
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





    //subCategory

    suspend fun getStoreSubCategory(id: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/SubCategory/${id}/${pageNumber}";
            val result = client.get(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<SubCategoryResponseDto>>())
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


    suspend fun createSubCategory(data: SubCategoryRequestDto): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/SubCategory/new";
            val result = client.post(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(data)
                contentType(ContentType.Application.Json)

            }

            if (result.status == HttpStatusCode.Created) {
                NetworkCallHandler.Successful(result.body<SubCategoryResponseDto>())
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


    suspend fun updateSubCategory(data: SubCategoryUpdateDto): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/SubCategory";
            val result = client.put(full_url) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
                setBody(data)
                contentType(ContentType.Application.Json)

            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<SubCategoryResponseDto>())
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


    suspend fun deleteSubCategory(subCategoryID: UUID): NetworkCallHandler {
        return try {
            val full_url = Secrets.getBaseUrl() + "/SubCategory/${subCategoryID}";
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

    suspend fun deleteUserAddress(addressID: UUID): NetworkCallHandler {
        return try {
            var result = client.delete(
                Secrets.getBaseUrl() + "/User/address/${addressID}"
            ) {
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

    suspend fun setAddressAsCurrent(addressId: UUID): NetworkCallHandler {
        return try {
            var result = client.post(
                Secrets.getBaseUrl() + "/User/address/active${addressId}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<Boolean>())
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

    //varient
    suspend fun getVarient(pageNumber: Int = 1): NetworkCallHandler {
        return try {
            var result = client.get(
                Secrets.getBaseUrl() + "/Varient/all/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }
            if (result.status == HttpStatusCode.OK) {
                return NetworkCallHandler.Successful(result.body<List<VarientResponseDto>>())
            } else if (result.status == HttpStatusCode.NoContent){
                return NetworkCallHandler.Error("No Data Found")
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