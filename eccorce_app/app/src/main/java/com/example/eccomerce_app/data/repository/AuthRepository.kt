package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.Dto.AuthResultDto
import com.example.eccomerce_app.dto.request.LoginDto
import com.example.eccomerce_app.dto.request.SignupDto
import com.example.eccomerce_app.util.Secrets
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.UnknownHostException

class AuthRepository(val client:HttpClient) {

    suspend fun login(loginData:LoginDto): NetworkCallHandler {
        return try {
            val full_url =Secrets.getBaseUrl()+"/User/login";
            val result = client.post(full_url){
                setBody(loginData)
                contentType(ContentType.Application.Json)
            }

            if(result.status== HttpStatusCode.OK){
                NetworkCallHandler.Successful(result.body<AuthResultDto>())
            }else{
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

    suspend fun signup(data: SignupDto): NetworkCallHandler {
        return try {
            val full_url =Secrets.getBaseUrl()+"/User/signup";
            val result = client.post(full_url){
                setBody(data)
                contentType(ContentType.Application.Json)
            }

            if(result.status== HttpStatusCode.Created){
                NetworkCallHandler.Successful(result.body<AuthResultDto>())
            }else{
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