package com.example.e_commerc_delivery_man.ui.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.Util.General.reachedBottom
import com.example.e_commerc_delivery_man.model.Address
import com.example.e_commerc_delivery_man.model.enMapType
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.CustomBotton
import com.example.e_commerc_delivery_man.ui.component.OrderComponent
import com.example.e_commerc_delivery_man.viewModel.OrderViewModel
import com.example.eccomerce_app.model.Order
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.system.exitProcess

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    nav: NavHostController,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp


    val orders = orderViewModel.myOrders.collectAsState()

    val coroutine = rememberCoroutineScope()
    val lazyState = rememberLazyListState()
    val state = rememberPullToRefreshState()


    val selectedId = remember { mutableStateOf(UUID.randomUUID()) }

    val isSendingData = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }
    val isLoadingMore = remember { mutableStateOf(false) }
    val reachedBottom = remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }


    val page = remember { mutableIntStateOf(1) }


    val snackBarHostState = remember { SnackbarHostState() }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { permission ->
            val arePermissionsGranted = permission.values.reduce { acc, next ->
                acc && next
            }
            if (arePermissionsGranted) {
                coroutine.launch(Dispatchers.Main) {

                    try {
                        val data = fusedLocationClient.lastLocation.await()
                        val selectedOrderData = orders.value?.firstOrNull() { it.id == selectedId }
                        data?.let { location ->
                            nav.navigate(
                                Screens.Map(
                                    lognit = selectedOrderData?.longitude,
                                    latitt = selectedOrderData?.latitude,
                                    title = selectedOrderData?.name,
                                    additionLat = location.latitude,
                                    additionLong = location.longitude,
                                    isFromLogin = false,
                                    mapType = enMapType.TrackOrder,
                                    id = selectedId.value.toString()
                                )
                            )

                        }


                    } catch (e: SecurityException) {
                        val error = "Permission exception: ${e.message}"
                    }

                }
            } else {
                coroutine.launch {
                    snackBarHostState.showSnackbar("لا بد من تفعيل صلاحية الموقع لاكمال العملية")
                }
            }
        })

    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permission ->
            if (permission) {
               nav.navigate(Screens.QrScanner)
            }
        }
    )




    LaunchedEffect(reachedBottom.value) {

        if (!orders.value.isNullOrEmpty() && reachedBottom.value && orders.value!!.size > 23) {
            orderViewModel.getMyOrders(
                page,
                isLoadingMore
            )
        }

    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(end = 15.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        "My Orders",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (24).sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center
                    )
                },

                )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier
//                    .padding(bottom = 20.dp)
                    .navigationBarsPadding()
            ) {
                Image(
                    imageVector = ImageVector
                        .vectorResource(
                            R.drawable.camera_scanner
                        ),
                    "",
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay

    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        if (isSendingData.value) Dialog(
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

        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                coroutine.launch {
                    if (!isRefresh.value) isRefresh.value = true
                    page.value = 1;
                    orderViewModel.getMyOrders(page)
                    if (isRefresh.value) {
                        delay(2000)
                        isRefresh.value = false
                    }

                }
            },
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            state = state,
            indicator = {
                Indicator(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .align(Alignment.TopCenter),
                    isRefreshing = isRefresh.value,
                    containerColor = Color.White,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    state = state
                )
            },
        ) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .background(Color.Gray.copy(alpha = 0.01f)),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!orders.value.isNullOrEmpty())
                    items(items = orders.value as List<Order>, key = { it -> it.id }) { order ->
                        OrderComponent(
                            order = order,
                            isCancel = true,
                            screenWidth = screenWidth,
                            isSendingData = isSendingData,
                            requestPermission = requestPermission,
                            snackBarHostState = snackBarHostState,
                            orderViewModel = orderViewModel,
                            selectedId = selectedId
                        )
                    }

                if (isLoadingMore.value) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        )
                        {
                            CircularProgressIndicator(color = CustomColor.primaryColor700)
                        }
                        Sizer(40)
                    }
                }


                item {
                    Box(modifier = Modifier.height(90.dp))
                }
            }

        }

    }

}

