package com.example.e_commerc_delivery_man.ui.view.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.e_commerc_delivery_man.model.Address
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Polyline

@SuppressLint("UnrememberedMutableState")
@Composable
fun MapScreen(userAddress: Address,deliveryAddress: Address){
    val deliveryLatLong =LatLng(
        deliveryAddress.latitude,
        deliveryAddress.longitude
    )
    val userLatLong=LatLng(
        userAddress.latitude,
        userAddress.longitude
    )
    val deliveryCamer = rememberCameraPositionState {

        position = CameraPosition.fromLatLngZoom(
            deliveryLatLong, 18f
        )
    }

    Scaffold {

        it.calculateTopPadding()
        it.calculateBottomPadding()
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = deliveryCamer,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {

            Polyline(points = listOf(
                userLatLong,
                deliveryLatLong
            ))

            Marker(
                state = MarkerState(position = deliveryLatLong),
                title = "my place"
            )
            Marker(
                state = MarkerState(position = userLatLong),
                title = "user place"
            )

                 }
    }
}