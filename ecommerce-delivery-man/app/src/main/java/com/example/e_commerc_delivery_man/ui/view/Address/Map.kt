package com.example.e_commerc_delivery_man.ui.view.Address


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.model.Address
import com.example.e_commerc_delivery_man.model.enMapType
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.CustomBotton
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.OrderViewModel
import com.example.e_commerc_delivery_man.viewModel.UserViewModel
import com.example.eccomerce_app.viewModel.MapViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberUpdatedMarkerState


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapHomeScreen(
    nav: NavHostController,
    userViewModel: UserViewModel,
    orderViewModel: OrderViewModel,
    mapViewModel: MapViewModel,
    title: String? = null,
    id: String? = null,
    longitude: Double?,
    latitude: Double?,
    additionLong: Double? = null,
    additionLat: Double? = null,
    mapType: enMapType = enMapType.My,
    isFomLogin: Boolean = true,
) {

    val context = LocalContext.current

    val directions = mapViewModel.googlePlaceInfo.collectAsState()
    val orders = orderViewModel.orders.collectAsState()
    val myOrders = orderViewModel.myOrders.collectAsState()
    val currentOrder = (myOrders.value ?: orders.value)?.firstOrNull { it.id.toString() == id };

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


    val coroutine = rememberCoroutineScope()


    val isLoading = remember { mutableStateOf(false) }
    val isOpenSheet = remember { mutableStateOf(false) }
    val isHasTitle = (mapType == enMapType.My)
    val isHasNavigationMap = !isHasTitle


    val additionLatLng = remember {
        mutableStateOf(
            if (additionLat == null) LatLng(0.0, 0.0)
            else LatLng(additionLat, additionLong!!)
        )
    }
    val additionLocation = rememberUpdatedMarkerState(
        position = additionLatLng.value
    )

    val mainLocation = rememberUpdatedMarkerState(
        position = LatLng(
            latitude ?: 15.347509735207755, longitude ?: 44.20684900134802
        )
    )

    val marker = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            mainLocation.position, 15f
        )
    }


    val snackBarHostState = remember { SnackbarHostState() }

    fun initial() {
        userViewModel.getMyInfo()
    }

    fun handleMapClick(point: LatLng) {
        Log.d("currentPressLocation", point.toString())
        when (mapType) {
            enMapType.My -> {
                marker.position = CameraPosition.fromLatLngZoom(point, 15f)
                mainLocation.position = point


            }

            else -> {}
        }
    }


    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L) // Desired interval: 500ms
            .setMinUpdateIntervalMillis(100L) // Fastest acceptable interval: 100ms
            .setMinUpdateDistanceMeters(1f).setWaitForAccurateLocation(false).build()
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                Log.d(
                    "ChangeLocation",
                    "${location.latitude.toString()} ${location.longitude.toString()}"
                )
                additionLatLng.value = LatLng(
                    location.latitude, location.longitude
                )
            }
        }
    }

    val requestPermissionThenNavigate = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { permissions ->
            val arePermissionsGranted = permissions.values.reduce { acc, next -> acc && next }

            if (arePermissionsGranted) {
                if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@rememberLauncherForActivityResult
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper()
                )

            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        })

    fun storeColorGenerator(index: Int): Color {
        val color = Color(
            red = (255 - index).coerceIn(0, 255),
            green = (255 - index).coerceIn(0, 255),
            blue = (255 - index).coerceIn(0, 255)
        )
        return color
    }

    fun updateDeliveryAddress() {
        val updateAddressData = Address(
            longitude = marker.position.target.longitude,
            latitude = marker.position.target.latitude,
        )


        coroutine.launch {
            isLoading.value = true
            isOpenSheet.value = false


            val result = async {
                userViewModel.updateDeliveryInfo(
                    address = updateAddressData
                )
            }.await()

            isLoading.value = false

            if (!result.isNullOrEmpty()) {
                snackBarHostState.showSnackbar(result)
                return@launch
            }

            snackBarHostState.showSnackbar(
                message = if (id.isNullOrEmpty()) "Address Add Successfully"
                else "Address Updated Successfully"
            )

            if (!isFomLogin) {
                nav.popBackStack()
                return@launch
            }

            userViewModel.userPassLocation(true)
            initial()
            nav.navigate(Screens.HomeGraph) {
                popUpTo(nav.graph.id) {
                    inclusive = true
                }
            }


        }

    }

    fun updateCameraToUser() {
        val newCameraPosition = CameraPosition.fromLatLngZoom(
            additionLocation.position,
            20f
        )

        coroutine.launch {
            marker.animate(update = CameraUpdateFactory.newCameraPosition(newCameraPosition))
        }
    }
    LaunchedEffect(Unit) {
        if (isHasNavigationMap) {
            Log.d("fromNavigationState", "True")
            mapViewModel.findPointBetweenTwoDestination(
                mainLocation.position,
                additionLocation.position,
                context.getString(R.string.google_map_token),
            )
        }
    }


    /*LaunchedEffect(Unit) {
        userViewModel.getMyInfo()
    }*/

    LaunchedEffect(Unit) {
        requestPermissionThenNavigate.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }



    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        floatingActionButton = {
            if (isHasNavigationMap)
                FloatingActionButton(
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp),
                    onClick = {
                        updateCameraToUser()
                    },
                    shape = RoundedCornerShape(8.dp),
                    containerColor = Color.White
                ) {
                    Image(
                        imageVector = ImageVector
                            .vectorResource(id = R.drawable.current_location),
                        contentDescription = "",
                        modifier = Modifier.size(25.dp)
                    )
                }
        },


        floatingActionButtonPosition = FabPosition.Start,
    ) { paddingValue ->
        paddingValue.calculateTopPadding()
        paddingValue.calculateBottomPadding()

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            val (bottomRef) = createRefs()


            LazyColumn(
                modifier = Modifier
                    .padding(
                        top = paddingValue.calculateTopPadding(),
                        bottom = paddingValue.calculateBottomPadding()
                    )
                    .background(Color.White)
                    .fillMaxSize()
            )
            {
                item {

                    GoogleMap(
                        modifier = Modifier.fillParentMaxSize(),
                        cameraPositionState = marker,
                        onMapClick = { latLng ->
                            handleMapClick(latLng)
                        },
                        properties = MapProperties(isMyLocationEnabled = false)
                    ) {
                        if (isHasNavigationMap == false) Marker(
                            state = MarkerState(position = mainLocation.position),
                            title = title,
                        )
                        else {


                            Marker(
                                state = MarkerState(position = additionLocation.position),
                                title = "My Place",
                            )

                            //this to display the store address that available at the map to let the delivery deside where to get the order from
                            currentOrder?.orderItems?.forEachIndexed { indexOrderItem, data ->
                                data.address?.forEachIndexed { index, addressData ->
                                    MarkerComposable(
                                        state = MarkerState(
                                            position =
                                                rememberUpdatedMarkerState(
                                                    position =
                                                        LatLng(
                                                            addressData.latitude,
                                                            addressData.longitude
                                                        )
                                                )
                                                    .position
                                        ),
                                        title = "${addressData.title} | ${data.product?.name?:""} : ${data.quantity}",
                                        onClick = { false }) {
                                        Image(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.store_icon),
                                            contentDescription = "",
                                            modifier = Modifier.size(40.dp),
                                            colorFilter = ColorFilter.tint(
                                                storeColorGenerator(
                                                    indexOrderItem
                                                )
                                            )
                                        )

                                    }
                                    Marker(
                                        state = MarkerState(position = additionLocation.position),
                                        title = "${addressData.title} | ${data.product?.name?:""} : ${data.quantity}",
                                    )
                                }
                            }

                            MarkerComposable(
                                state = MarkerState(position = mainLocation.position),
                                title = currentOrder?.name ?: "Order Owner",
                            ) {
                                Image(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.user_current_icon),
                                    contentDescription = "",
                                    modifier = Modifier.size(40.dp)
                                )

                            }
                        }

                        if (!directions.value.isNullOrEmpty()) Polyline(
                            directions.value!!, color = Color.Red, pattern = listOf(
                                Dash(15f), Gap(2f)
                            )
                        )
                    }


                }
            }



            if (isHasTitle) CustomBotton(
                buttonTitle = if (!id.isNullOrEmpty()) "Edite Current Location" else "Add New Address",
                color = CustomColor.primaryColor700,
                isEnable = true,
                modifier = Modifier
                    .padding(bottom = paddingValue.calculateBottomPadding() + 10.dp)

                    .height(50.dp)
                    .fillMaxWidth(0.9f)
                    .constrainAs(bottomRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },

                isLoading = false,
                operation = { updateDeliveryAddress() },
                labelSize = 20
            )

        }
    }

    if (isLoading.value) Dialog(
        onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .height(90.dp)
                .width(90.dp)
                .background(
                    Color.White, RoundedCornerShape(15.dp)
                ), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = CustomColor.primaryColor700, modifier = Modifier.size(40.dp)
            )
        }
    }
}
