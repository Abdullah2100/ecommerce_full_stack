package com.example.e_commerc_delivery_man.ui.view.home

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.OrderViewModel
import com.example.e_commerc_delivery_man.Util.General.reachedBottom
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.model.Address
import com.example.e_commerc_delivery_man.ui.component.CustomBotton
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    nav: NavHostController,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val coroutine = rememberCoroutineScope()
    val lazyState = rememberLazyListState()

    val orders = orderViewModel.orders.collectAsState()


    val snackBarHostState = remember { SnackbarHostState() }

    val isSendingData = remember { mutableStateOf(false) }
    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }
    val reachedBottom = remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }


    val deletedId = remember { mutableStateOf<UUID?>(null) }


    val page = remember { mutableStateOf(1) }

    val address = remember { mutableStateOf<Address?>(null) }


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
                        data?.let { location ->

                            val longint = location.longitude ?: 5.5000
                            val latit = location.latitude ?: 5.5000
                            address.value = Address(longint, latit)
                        }
                        if (address.value == null) {
                            address.value = Address(5.5, 5.3)

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



    LaunchedEffect(reachedBottom.value) {

        if (!orders.value.isNullOrEmpty() && reachedBottom.value) {
            orderViewModel.getOrders(
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
                        "Orders",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (24).sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center
                    )
                },

                )
        },
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
            onRefresh = { {} },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .background(Color.Gray.copy(alpha = 0.01f)),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    orders.value?.forEach { order ->
                        Log.d("imageUrl", order.toString())

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                                .shadow(8.dp)
                                .background(
                                    Color.White,
                                    RoundedCornerShape(8.dp)
                                )

                                .padding(horizontal = 5.dp, vertical = 10.dp)

                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        order.name,
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 25.sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        order.userPhone,
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center

                                    )

                                    Row {
                                        Text(
                                            "OrderItems :",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )

                                        Text(
                                            "${order.orderItems.size}",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )
                                    }


                                    Row {
                                        Text(
                                            "Total Price : ",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )

                                        Text(
                                            "${order.totalPrice}",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )
                                    }

                                    Row {
                                        Text(
                                            "DeliveryFee : ",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )

                                        Text(
                                            "${order.deliveryFee}",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )
                                    }

                                    Row {
                                        Text(
                                            "realPayed : ",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )

                                        Text(
                                            "${order.realPrice}",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )
                                    }


                                }

                                IconButton({
                                    requestPermission.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }) {
                                    Icon(
                                        ImageVector.vectorResource(R.drawable.location),
                                        "",
                                        tint = CustomColor.primaryColor500
                                    )
                                }
                            }


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Box(
                                    Modifier
                                        .width(((screenWidth) - 20).dp)
                                ) {
                                    CustomBotton(

                                        buttonTitle = "Except Order",
                                        operation = {
                                            deletedId.value = order.id
                                            coroutine.launch {
                                                isSendingData.value = true;
                                                val result = async {
                                                    orderViewModel.takeOrder(order.id)
                                                }.await()
                                                isSendingData.value = false
                                                if (!result.isNullOrEmpty()) {
                                                   snackBarHostState
                                                        .showSnackbar(result)
                                                }
                                            }

                                        },
                                        color = CustomColor.alertColor_2_500,
//                                        isLoading = isSendingData.value && deletedId.value == order.id
                                    )
                                }

                            }

                        }
                    }
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

