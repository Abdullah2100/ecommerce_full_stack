package com.example.e_commercompose.ui.view.location

import android.graphics.BitmapFactory
import android.util.Log
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
import com.example.e_commercompose.viewModel.HomeViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun MapHomeScreen(
    nav: NavHostController,
    homeViewModle: HomeViewModel,
    title: String? = null,
    id: String? = null,
    lognit: Double?,
    latitt: Double?,
    isFomLogin: Boolean = true,
) {

    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    val isOpenSheet = remember { mutableStateOf(false) }

    val addressTitle = remember { mutableStateOf<TextFieldValue>(TextFieldValue(title ?: "")) }

    val markers =
        remember { mutableStateOf<Point?>(Point.fromLngLat(lognit ?: -98.0, latitt ?: 39.5)) }

    val coroutine = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(lognit ?: -98.0, latitt ?: 39.5)) // Example location
            zoom(20.0)
        }
    }

    LaunchedEffect(Unit) {
        homeViewModle.getMyInfo()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                            placHolder = addressTitle.value.text.ifEmpty { "Write Address Name" },
                            title = "Address Title"
                        )
                        Sizer(10)
                        CustomBotton(
                            operation = {
                                coroutine.launch {
                                    isLoading.value = true;
                                    isOpenSheet.value = false;
                                    val result = async {
                                        if (id.isNullOrEmpty())
                                            homeViewModle
                                                .addUserAddress(
                                                    longit = markers.value?.longitude(),
                                                    latit = markers.value?.latitude(),
                                                    title = addressTitle.value.text

                                                ) else homeViewModle.updateUserAddress(
                                            addressId = UUID.fromString(id),
                                            addressTitle = addressTitle.value.text,
                                            longit = markers.value?.longitude(),
                                            latit = markers.value?.latitude(),

                                            )
                                    }.await()
                                    isLoading.value = false
                                    if (!result.isNullOrEmpty()) {
                                        snackbarHostState.showSnackbar(result)
                                        return@launch;
                                    }

                                    snackbarHostState.showSnackbar(
                                        if (id.isNullOrEmpty()) "Address Add Seccessfuly"
                                        else "Address Updated Successfully"
                                    )

                                    if (!isFomLogin) {
                                        nav.popBackStack()
                                        return@launch
                                    }

                                    homeViewModle.userPassLocation()
                                    homeViewModle.initialFun()
                                    nav.navigate(Screens.HomeGraph) {
                                        popUpTo(nav.graph.id) {
                                            inclusive = true
                                        }
                                    }


                                }

                            },
                            buttonTitle = if (id.isNullOrEmpty()) "Add" else "Update",
                            color = CustomColor.primaryColor700
                        )
                        Sizer(10)
                    }
                }
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        ConstraintLayout {
            val (bottonRef) = createRefs()
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
                    markers.value = value
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
            CustomBotton(
                buttonTitle = if (!id.isNullOrEmpty()) "Edite Current Location" else "Add New Address",
                color = CustomColor.primaryColor700,
                isEnable = true,
                modifier = Modifier
                    .padding(bottom = it.calculateBottomPadding())

                    .height(50.dp)
                    .fillMaxWidth(0.9f)
                    .constrainAs(bottonRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },

                isLoading = false,
                operation = {
                    isOpenSheet.value = true;
                },
                lableSize = 20
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

