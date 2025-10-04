package com.example.eccomerce_app.dto

import kotlinx.serialization.Serializable

@Serializable
data class GooglePlacesInfo(
    val geocoded_waypoints: List<GeocodedWaypoints>?,
    val routes: List<Routes>,
    val status: String
)

@Serializable
data class GeocodedWaypoints(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
) {

}

@Serializable
data class Routes(
    val summary: String,
    val overview_polyline: OverviewPolyline,
    val legs: List<Legs>
)

@Serializable
data class OverviewPolyline(
    val points: String
)

@Serializable
data class Legs(
    val distance: Distance,
    val duration: Duration
)


@Serializable
data class Distance(
    val text: String,
    val value: Int
)

@Serializable
data class Duration(
    val text: String,
    val value: Int
)

