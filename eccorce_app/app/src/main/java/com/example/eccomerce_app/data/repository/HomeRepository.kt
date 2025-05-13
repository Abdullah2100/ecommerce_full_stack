package com.example.eccomerce_app.data.repository

import android.R
import com.example.eccomerce_app.Dto.AuthResultDto
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.dto.request.LocationRequestDto
import com.example.eccomerce_app.dto.request.LoginDto
import com.example.eccomerce_app.dto.request.SignupDto
import com.example.eccomerce_app.dto.response.AddressResponseDto
import com.example.eccomerce_app.dto.response.CategoryReponseDto
import com.example.eccomerce_app.dto.response.ErrorResponse
import com.example.eccomerce_app.util.Secrets
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class HomeRepository(val client: HttpClient) {

    suspend fun getUserLocations(): NetworkCallHandler {
        return try {
            var result = client.get(
                Secrets.getBaseUrl() + "/User/location"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if(result.status== HttpStatusCode.OK)
            {
                NetworkCallHandler.Successful(result.body<List<AddressResponseDto>>())
            }
            else{
                NetworkCallHandler.Error(
                    result.body<String>()
                )
            }

        }catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun userAddNewLocation(locationData:LocationRequestDto): NetworkCallHandler {
        return try {
            var result = client.post(
                Secrets.getBaseUrl() + "/User/location"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if(result.status== HttpStatusCode.OK)
            {
                NetworkCallHandler.Successful(result.body<AddressResponseDto?>())
            }
            else{
                NetworkCallHandler.Error(
                    result.body<String>()
                )
            }

        }catch (e: UnknownHostException) {

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
                Secrets.getBaseUrl() + "/User/location/current${addressId}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            if(result.status== HttpStatusCode.OK)
            {
                NetworkCallHandler.Successful(result.body<Boolean>())
            }
            else{
                NetworkCallHandler.Error(
                    result.body<String>()
                )
            }

        }catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }


    suspend fun getCategory(pageNumber:Int=1): NetworkCallHandler {
        return try {
            var result = client.get(
                Secrets.getBaseUrl()+"/Category/all${pageNumber}"
            ) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }
            if(result.status== HttpStatusCode.OK)
            {
                return NetworkCallHandler.Successful(result.body<List<CategoryReponseDto>>())
            }
            else{
                return NetworkCallHandler.Error(result.body())
            }

        }
        catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }
}