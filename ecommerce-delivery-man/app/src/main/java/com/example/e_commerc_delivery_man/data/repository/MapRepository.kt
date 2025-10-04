package com.example.eccomerce_app.data.repository

import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.UnknownHostException

class MapRepository(val client: HttpClient) {

    suspend fun getDistanceBetweenTwoPoint(
        origin: LatLng,
        destination: LatLng,
        key: String
    ): NetworkCallHandler {
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&key=$key"

        return try {
            val result = client.get(url)
            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<GooglePlacesInfo>())
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

}