package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.dto.GooglePlacesInfo
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.google.android.gms.maps.model.LatLng
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.UnknownHostException

class MapRepository(val client: HttpClient) {

    suspend fun getDistanceBetweenTwoPoint(
        origin: LatLng,
        destination: LatLng,
        key: String,
        wayPoint: List<LatLng>? = null
    ): NetworkCallHandler {
        // Prepare waypoints parameter string if wayPoint list is provided
        val waypointsParam = wayPoint?.takeIf { it.isNotEmpty() }
            ?.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }

        // Build URL with waypoints if available
        val url = buildString {
            append("https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}")
            append("&destination=${destination.latitude},${destination.longitude}")
            if (waypointsParam != null) {
                append("&waypoints=$waypointsParam")
            }
            append("&key=$key")
        }

        return try {
            val result = client.get(url)
            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<GooglePlacesInfo>())
                }
                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }
        } catch (e: UnknownHostException) {
            NetworkCallHandler.Error(e.message)
        } catch (e: IOException) {
            NetworkCallHandler.Error(e.message)
        } catch (e: Exception) {
            NetworkCallHandler.Error(e.message)
        }
    }

}