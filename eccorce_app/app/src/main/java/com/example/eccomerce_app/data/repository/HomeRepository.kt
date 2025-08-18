package com.example.e_commercompose.data.repository

import com.example.e_commercompose.Util.General
import com.example.eccomerce_app.dto.AddressDto
import com.example.eccomerce_app.dto.BannerDto
import com.example.eccomerce_app.dto.CategoryDto
import com.example.eccomerce_app.dto.ProductDto
import com.example.eccomerce_app.dto.StoreDto
import com.example.eccomerce_app.dto.SubCategoryDto
import com.example.eccomerce_app.dto.UserDto
import com.example.eccomerce_app.dto.VarientDto
import com.example.e_commercompose.model.MyInfoUpdate
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.dto.GeneralSettingDto
import com.example.eccomerce_app.dto.OrderItemDto
import com.example.eccomerce_app.dto.OrderDto
import com.example.eccomerce_app.dto.CreateAddressDto
import com.example.eccomerce_app.dto.CreateOrderDto
import com.example.eccomerce_app.dto.CreateSubCategoryDto
import com.example.eccomerce_app.dto.UpdateAddressDto
import com.example.eccomerce_app.dto.UpdateOrderItemStatusDto
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
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

    //Banner
    suspend fun getBannerByStoreId(storeId: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Banner/${storeId}/${pageNumber}";
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.OK) {
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
            val fullUrl = Secrets.getBaseUrl() + "/Banner";
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }

            }

            if (result.status == HttpStatusCode.OK) {
                NetworkCallHandler.Successful(result.body<List<BannerDto>>())
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


    suspend fun createBanner(endDate: String, image: File): NetworkCallHandler {
        return try {
            val result = client.post(
                Secrets.getBaseUrl() + "/Banner"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
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
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }

            }
            if (result.status == HttpStatusCode.NoContent) {
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


    //category
    suspend fun getCategory(pageNumber: Int = 1): NetworkCallHandler {
        return try {
            val result = client.get(
                Secrets.getBaseUrl() + "/Category/all/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
            }
            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<List<CategoryDto>>())
                }

                HttpStatusCode.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")

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

    //general
    suspend fun getGeneral(pageNumber: Int = 1): NetworkCallHandler {
        return try {
            val result = client.get(
                Secrets.getBaseUrl() + "/General/all/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
            }
            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<List<GeneralSettingDto>>())
                }

                HttpStatusCode.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")

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


    //order
    suspend fun submitOrder(cartData: CreateOrderDto): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Order";
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
            val fullUrl = Secrets.getBaseUrl() + "/Order/me/${pageNumber}";
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
            val fullUrl = Secrets.getBaseUrl() + "/Order/${orderId}";
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

    suspend fun getMyOrderItemForStoreId(storeId: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Order/orderItem/${storeId}/${pageNumber}";
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

                    NetworkCallHandler.Successful(result.body<List<OrderItemDto>>())

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

    suspend fun updateOrderItemStatus(id: UUID, status: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Order/orderItem/statsu";
            val result = client.put(fullUrl) {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
                setBody(UpdateOrderItemStatusDto(id, status))
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


    //product

    suspend fun getProduct(pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Product/all/${pageNumber}";
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
                    NetworkCallHandler.Successful(result.body<List<ProductDto>>())
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

    suspend fun getProductByCategoryId(categoryId: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Product/category/${categoryId}/${pageNumber}";
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
                    NetworkCallHandler.Successful(result.body<List<ProductDto>>())
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

    suspend fun getProduct(storeId: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Product/${storeId}/${pageNumber}";
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
                    NetworkCallHandler.Successful(result.body<List<ProductDto>>())
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

    suspend fun getProduct(storeId: UUID, subCategory: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl =
                Secrets.getBaseUrl() + "/Product/${storeId}/${subCategory}/${pageNumber}";
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
                    NetworkCallHandler.Successful(result.body<List<ProductDto>>())
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


    suspend fun createProduct(
        name: String,
        description: String,
        thumbnail: File,
        subcategoryId: UUID,
        storeId: UUID,
        price: Double,
        productVarients: List<ProductVarientSelection>,
        images: List<File>
    ): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Product";
            val result = client.post(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("name", name)
                            append("description", description)
                            append(
                                key = "thmbnail", // Must match backend expectation
                                value = thumbnail.readBytes(),
                                headers = Headers.build {
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

                            append("subcategoryId", subcategoryId.toString())
                            append("storeId", storeId.toString())
                            append("price", price)
                            if (productVarients.isNotEmpty())
                                productVarients.forEachIndexed { it, value ->
                                    append("productVarients[${it}].name", value.name)
                                    append("productVarients[${it}].precentage", value.precentage!!)
                                    append(
                                        "productVarients[${it}].varientId",
                                        value.varientId.toString()
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

            when (result.status) {
                HttpStatusCode.Created -> {
                    NetworkCallHandler.Successful(result.body<ProductDto>())
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

    suspend fun updateProduct(
        id: UUID,
        name: String?,
        description: String?,
        thumbnail: File?,
        subcategoryId: UUID?,
        storeId: UUID,
        price: Double?,
        productVarients: List<ProductVarientSelection>?,
        images: List<File>?,
        deletedProductVarients: List<ProductVarientSelection>?,
        deleteImages: List<String>?
    ): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Product";
            val result = client.put(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
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
                            if (thumbnail != null)
                                append(
                                    key = "thmbnail", // Must match backend expectation
                                    value = thumbnail.readBytes(),
                                    headers = Headers.build {
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
                            if (subcategoryId != null)
                                append("subcategoryId", subcategoryId.toString())
                            append("storeId", storeId.toString())

                            if (price != null)
                                append("price", price)

                            if (!productVarients.isNullOrEmpty())
                                productVarients.forEachIndexed { it, value ->
                                    append("productVarients[${it}].name", value.name)
                                    append("productVarients[${it}].precentage", value.precentage!!)
                                    append(
                                        "productVarients[${it}].varientId",
                                        value.varientId.toString()
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
                                        value.varientId.toString()
                                    )

                                }

                            if (!deleteImages.isNullOrEmpty())
                                deleteImages.forEachIndexed { it, value ->
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

            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<ProductDto>())
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

    suspend fun deleteProduct(storeId: UUID, productId: UUID): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Product/${storeId}/${productId}";
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


    //store
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
                        "Bearer ${General.authData.value?.RefreshToken}"
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
                                headers = Headers.build {
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
                                headers = Headers.build {
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
                HttpStatusCode.Created -> {
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
                        "Bearer ${General.authData.value?.RefreshToken}"
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
                                    headers = Headers.build {
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
                                    headers = Headers.build {
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
                HttpStatusCode.OK -> {
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
            val fullUrl = Secrets.getBaseUrl() + "/Store/${id}";
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

    suspend fun getStoreAddress(id: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Store/${id}/${pageNumber}";
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


    //subCategory

    suspend fun getStoreSubCategory(id: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/SubCategory/${id}/${pageNumber}";
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
                    NetworkCallHandler.Successful(result.body<List<SubCategoryDto>>())
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


    suspend fun createSubCategory(data: CreateSubCategoryDto): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/SubCategory/new";
            val result = client.post(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
                setBody(data)
                contentType(ContentType.Application.Json)

            }

            when (result.status) {
                HttpStatusCode.Created -> {
                    NetworkCallHandler.Successful(result.body<SubCategoryDto>())
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


    suspend fun updateSubCategory(data: SubCategoryDto): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/SubCategory";
            val result = client.put(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
                setBody(data)
                contentType(ContentType.Application.Json)

            }

            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<SubCategoryDto>())
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


    suspend fun deleteSubCategory(subCategoryID: UUID): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/SubCategory/${subCategoryID}";
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


    //user
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


    suspend fun getMyInfo(): NetworkCallHandler {
        return try {
            val result = client.get(
                Secrets.getBaseUrl() + "/User/me"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
            }
            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<UserDto>())
                }

                else -> NetworkCallHandler.Error(result.body())
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
            val result = client.put(
                Secrets.getBaseUrl() + "/User"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
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
            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<UserDto>())
                }

                else -> NetworkCallHandler.Error(result.body())
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
            val result = client.get(
                Secrets.getBaseUrl() + "/Varient/all/${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.RefreshToken}"
                    )
                }
            }
            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<List<VarientDto>>())
                }

                HttpStatusCode.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")
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


}