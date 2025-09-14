package com.example.eccomerce_app.ui.view.address

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.e_commercompose.ui.theme.CustomColor
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.example.e_commercompose.R
import com.example.e_commercompose.model.enMapType
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.StoreViewModel
import com.example.eccomerce_app.viewModel.VariantViewModel
import com.example.eccomerce_app.viewModel.BannerViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import com.example.eccomerce_app.viewModel.GeneralSettingViewModel
import com.example.eccomerce_app.viewModel.OrderViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
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
    title: String? = null,
    id: String? = null,
    longitude: Double?,
    latitude: Double?,
    mapType: enMapType = enMapType.My,
    isFomLogin: Boolean = true,

    ) {

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    val coroutine = rememberCoroutineScope()


    val isLoading = remember { mutableStateOf(false) }
    val isHasError = remember { mutableStateOf(false) }
    val isOpenSheet = remember { mutableStateOf(false) }
    val isHasTitle = (mapType == enMapType.My)

    val errorMessage = remember { mutableStateOf("") }

    val addressTitle = remember { mutableStateOf(TextFieldValue(title ?: "")) }

    val markers =
        remember { mutableStateOf<Point?>(Point.fromLngLat(longitude ?: -98.0, latitude ?: 39.5)) }

    val snackBarHostState = remember { SnackbarHostState() }
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(longitude ?: -98.0, latitude ?: 39.5)) // Example location
            zoom(20.0)
        }
    }


    fun initial() {
        userViewModel.getMyInfo()
        generalSettingViewModel.getGeneral(1)
        categoryViewModel.getCategories(1)
        bannerViewModel.getStoresBanner()
        variantViewModel.getVariants(1)
        productViewModel.getProducts(mutableIntStateOf(1))
        orderViewModel.getMyOrders(mutableIntStateOf(1))
    }

    fun handleMapClick(point: Point) {
        when (mapType) {
            enMapType.My -> markers.value = point
            enMapType.MyStore -> {
                coroutine.launch {
                    storeViewModel.setStoreCreateData(point.longitude(), point.latitude())
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

    LaunchedEffect(Unit) {
        userViewModel.getMyInfo()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        bottomBar = {
            if (isOpenSheet.value)
                ModalBottomSheet(
                    onDismissRequest = {
                        isOpenSheet.value = false
                    },
                    sheetState = sheetState,

                    ) {

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                    ) {

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
                                        if (id.isNullOrEmpty())
                                            userViewModel
                                                .addUserAddress(
                                                    longitude = markers.value?.longitude(),
                                                    latitude = markers.value?.latitude(),
                                                    title = addressTitle.value.text
                                                )
                                        else userViewModel.updateUserAddress(
                                            addressId = UUID.fromString(id),
                                            addressTitle = addressTitle.value.text,
                                            longitude = markers.value?.longitude(),
                                            latitude = markers.value?.latitude(),

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
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        ConstraintLayout {
            val (bottomRef) = createRefs()
            MapboxMap(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize(),
                mapViewportState = mapViewportState,
                mapInitOptionsFactory = { context ->
                    MapInitOptions(
                        context = context,
                        // Other parameters typically defaults or null, avoid passing unsupported sig
                        styleUri = Style.MAPBOX_STREETS,

                        )
                },

                onMapClickListener = { value ->
                    //this to make ability to track with map only for my or myStore
                    handleMapClick(value)
                    true
                },

                ) {
                markers.value?.let { point ->
                    PointAnnotationGroup(
                        annotations = listOf(
                            PointAnnotationOptions()
                                .withPoint(point)
                                .withIconImage(
                                    BitmapFactory
                                        .decodeResource(
                                            context.resources,
                                            R.drawable.vector
                                        )
                                )
                        )
                    )
                }
            }

            if (isHasTitle)
                CustomBotton(
                    buttonTitle = if (!id.isNullOrEmpty()) "Edite Current Location" else "Add New Address",
                    color = CustomColor.primaryColor700,
                    isEnable = true,
                    modifier = Modifier
                        .padding(bottom = it.calculateBottomPadding() + 10.dp)

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
        if (isLoading.value)
            Dialog(
                onDismissRequest = {}
            ) {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(90.dp)
                        .background(
                            Color.White,
                            RoundedCornerShape(15.dp)
                        ), contentAlignment = Alignment.Center
                )
                {
                    CircularProgressIndicator(
                        color = CustomColor.primaryColor700,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
    }


}