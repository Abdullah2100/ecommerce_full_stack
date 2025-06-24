package com.example.e_commerc_delivery_man.data.repository

import com.example.e_commerc_delivery_man.Dto.AuthResultDto
import com.example.e_commerc_delivery_man.dto.request.LoginDto
import com.example.e_commerc_delivery_man.dto.request.SignupDto
import com.example.e_commerc_delivery_man.util.Secrets
import com.example.eccomerce_app.dto.request.ForgetPasswordDto
import com.example.eccomerce_app.dto.request.ReseatPasswordRequestDto
import com.example.eccomerce_app.dto.request.VerificationRequestDto
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
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

    suspend fun getOtp(email: String): NetworkCallHandler {
        return try {
            val full_url =Secrets.getBaseUrl()+"/User/generateOtp";
            val result = client.post(full_url){
                setBody(ForgetPasswordDto(email))
                contentType(ContentType.Application.Json)
            }

            if(result.status== HttpStatusCode.NoContent){
                NetworkCallHandler.Successful(true)
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



    suspend fun verifingOtp(email: String,otp: String): NetworkCallHandler {
        return try {
            val full_url =Secrets.getBaseUrl()+"/User/otpVerification";
            val result = client.post(full_url){
                setBody(VerificationRequestDto(email,otp))
                contentType(ContentType.Application.Json)
            }

            if(result.status== HttpStatusCode.NoContent){
                NetworkCallHandler.Successful(true)
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

    suspend fun reasetPassword(email:String,otp: String,newPassword: String): NetworkCallHandler {
        return try {
            val full_url =Secrets.getBaseUrl()+"/User/reseatPassword";
            val result = client.post(full_url){
                setBody(ReseatPasswordRequestDto(
                    email = email,
                    otp = otp,
                    password =newPassword
                ))
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



}