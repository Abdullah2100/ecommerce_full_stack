package com.example.eccomerce_app.ui.view.address


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.e_commercompose.model.enMapType
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.viewModel.BannerViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import com.example.eccomerce_app.viewModel.GeneralSettingViewModel
import com.example.eccomerce_app.viewModel.OrderViewModel
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.StoreViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import com.example.eccomerce_app.viewModel.VariantViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.e_commercompose.R
import com.example.eccomerce_app.viewModel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberUpdatedMarkerState


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapHomeScreen(
    nav: NavHostController,
    userViewModel: UserViewModel,
    storeViewModel: StoreViewModel,
    generalSettingViewModel: GeneralSettingViewModel,
    categoryViewModel: CategoryViewModel,
    orderViewModel: OrderViewModel,
    bannerViewModel: BannerViewModel,
    variantViewModel: VariantViewModel,
    productViewModel: ProductViewModel,
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

    val sheetState = rememberModalBottomSheetState()

    val coroutine = rememberCoroutineScope()


    val isLoading = remember { mutableStateOf(false) }
    val isHasError = remember { mutableStateOf(false) }
    val isOpenSheet = remember { mutableStateOf(false) }
    val isHasTitle = (mapType == enMapType.My)
    val isHasNavigationMap = (mapType == enMapType.Store || mapType == enMapType.TrackOrder)


    val errorMessage = remember { mutableStateOf("") }

    val addressTitle = remember { mutableStateOf(TextFieldValue(title ?: "")) }

    val additionLocation = rememberUpdatedMarkerState(
        position = if (additionLat == null) LatLng(0.0, 0.0)
        else LatLng(additionLat, additionLong!!)
    )
    val mainLocation = rememberUpdatedMarkerState(
        position = LatLng(
            latitude ?: 15.347509735207755,
            longitude ?: 44.20684900134802
        )
    )
    val marker = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            mainLocation.position, 15f
        )
    }


    val snackBarHostState = remember { SnackbarHostState() }


    Log.d(
        "LocationClick",
        "${mainLocation.position.toString()} ${additionLocation.position.toString()}"
    )

    fun initial() {
        userViewModel.getMyInfo()
        generalSettingViewModel.getGeneral(1)
        categoryViewModel.getCategories(1)
        bannerViewModel.getStoresBanner()
        variantViewModel.getVariants(1)
        productViewModel.getProducts(mutableIntStateOf(1))
        orderViewModel.getMyOrders(mutableIntStateOf(1))
    }

    fun handleMapClick(point: LatLng) {
        Log.d("LocationClick", point.toString())
        when (mapType) {
            enMapType.My -> {
                marker.position = CameraPosition.fromLatLngZoom(point, 15f)
                mainLocation.position = point
            }

            enMapType.MyStore -> {
                coroutine.launch {
                    storeViewModel.setStoreCreateData(point.longitude, point.latitude)
                    nav.popBackStack()
                }
            }

            else -> {}
        }
    }

    fun validateUserAddressTitle(): Boolean {
        isHasError.value = false;
        errorMessage.value = ""
        when (addressTitle.value.text.isEmpty()) {
            true -> {
                isHasError.value = true;
                errorMessage.value = "Address Title mustn't be empty"
                return false
            }

            else -> {
                return true;
            }
        }
    }

    fun updateCameraToUser() {
        val newCameraPosition = CameraPosition.fromLatLngZoom(
            additionLocation.position,
            20f
        ) // New position and zoom level

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


    LaunchedEffect(Unit) {
        userViewModel.getMyInfo()
    }




    Scaffold(
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
        bottomBar = {
            if (isOpenSheet.value) ModalBottomSheet(
                onDismissRequest = {
                    isOpenSheet.value = false
                },
                sheetState = sheetState,

                ) {

                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                )
                {

                    TextInputWithTitle(
                        value = addressTitle,
                        title = "Address Title",
                        placeHolder = addressTitle.value.text.ifEmpty { "Write Address Name" },
                        errorMessage = errorMessage.value,
                        isHasError = isHasError.value,

                        )
                    Sizer(10)
                    CustomAuthBottom(
                        operation = {
                            coroutine.launch {
                                isLoading.value = true
                                isOpenSheet.value = false
                                val result = async {
                                    if (id.isNullOrEmpty()) userViewModel.addUserAddress(
                                        longitude = marker.position.target.longitude,
                                        latitude = marker.position.target.latitude,
                                        title = addressTitle.value.text
                                    )
                                    else userViewModel.updateUserAddress(
                                        addressId = UUID.fromString(id),
                                        addressTitle = addressTitle.value.text,
                                        longitude = marker.position.target.longitude,
                                        latitude = marker.position.target.latitude,

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

                        },
                        buttonTitle = if (id.isNullOrEmpty()) "Add" else "Update",
                        validationFun = {
                            validateUserAddressTitle()
                        },
                        isLoading = isLoading.value
                    )
                    Sizer(10)
                }
            }
        }) { paddingValue ->
        paddingValue.calculateTopPadding()
        paddingValue.calculateBottomPadding()

        ConstraintLayout {
            val (bottomRef) = createRefs()

            Box(
                modifier = Modifier
                    .padding(
                        top = paddingValue.calculateTopPadding(),
                        bottom = paddingValue.calculateBottomPadding()
                    )
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = marker,
                    onMapClick = { latLng ->
                        handleMapClick(latLng)
                    })
                {
                    if (isHasNavigationMap == false)
                        Marker(
                            state = MarkerState(position = mainLocation.position),
                            title = title,
                        )
                    else {
                        Marker(
                            state = MarkerState(position = additionLocation.position),
                            title = "My Place",
                        )

                        MarkerComposable(
                            state = MarkerState(position = mainLocation.position),

                            title = title,
                            onClick = {
                                true
                            }
                        ) {
                            Image(
                                imageVector = ImageVector
                                    .vectorResource(id = R.drawable.store_icon),
                                contentDescription = "",
                                modifier = Modifier.size(20.dp)
                            )

                        }
                    }
                    if (!directions.value.isNullOrEmpty())
                        Polyline(
                            directions.value!!,
                            color = Color.Red,
                            pattern = listOf(
                                Dash(15f), Gap(2f)
                            )
                        )
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
                operation = {
                    when (isHasTitle) {
                        true -> {
                            isOpenSheet.value = true
                        }

                        else -> {

                        }
                    }
                },
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
