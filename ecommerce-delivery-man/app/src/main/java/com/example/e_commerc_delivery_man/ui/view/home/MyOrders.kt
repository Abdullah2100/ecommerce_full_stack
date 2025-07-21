package com.example.e_commerc_delivery_man.ui.view.home

import android.Manifest
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.Util.General.reachedBottom
import com.example.e_commerc_delivery_man.model.Address
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.CustomBotton
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {
    var context = LocalContext.current
    var config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp


    var orders = homeViewModel.myOrders.collectAsState()
    var varient = homeViewModel.varients.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }


    val isSendingData = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }
    val isExpanded = remember { mutableStateOf(false) }


    var roatation = animateFloatAsState(
        if (isExpanded.value) 180f else 0f
    )

    val deletedId = remember { mutableStateOf<UUID?>(null) }
    val currutine = rememberCoroutineScope()


    val lazyState = rememberLazyListState()
    val reachedBottom = remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }
    var page = remember { mutableStateOf(1) }
    val isLoadingMore = remember { mutableStateOf(false) }


    Log.d("loadingState", isLoadingMore.value.toString())


    val address = remember { mutableStateOf<Address?>(null) }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val requestPermssion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { permission ->
            val arePermissionsGranted = permission.values.reduce { acc, next ->
                acc && next
            }
            if (arePermissionsGranted) {
                currutine.launch(Dispatchers.Main) {

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
                        var error = "Permission exception: ${e.message}"
                    }

                }
            } else {
                currutine.launch {
                    snackbarHostState.showSnackbar("لا بد من تفعيل صلاحية الموقع لاكمال العملية")
                }
            }
        })



    LaunchedEffect(reachedBottom.value) {

//        if(!orders.value.isNullOrEmpty() && reachedBottom.value){
//            homeViewModel.getMyOrder(
//                page,
//                isLoadingMore
//            )
//        }

    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 65.dp)
                    .offset(x = 16.dp),
            ) {

            }
        }
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
            onRefresh = { {} }
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
                                    requestPermssion.launch(
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

                            AnimatedVisibility(
                                visible = isExpanded.value,
                                enter = expandVertically(
                                    expandFrom = Alignment.Top,
                                    animationSpec = tween()
                                ) + fadeIn(),
                                exit = shrinkVertically(
                                    shrinkTowards = Alignment.Top,
                                    animationSpec = tween()
                                ) + fadeOut()

                            ) {

                                order.orderItems
                                    .groupBy { it.product.storeId }
                                    .values
                                    .forEach { it ->
                                        it.forEach { orderItems ->
                                            Column {
                                                Row(modifier = Modifier.padding(top = 10.dp)) {
                                                    SubcomposeAsyncImage(
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .height(80.dp)
                                                            .width(80.dp)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        model = General.handlingImageForCoil(
                                                            orderItems.product.thmbnail,
                                                            context
                                                        ),
                                                        contentDescription = "",
                                                        loading = {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxSize(),
                                                                contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                                            ) {
                                                                CircularProgressIndicator(
                                                                    color = Color.Black,
                                                                    modifier = Modifier.size(53.dp) // Adjust the size here
                                                                )
                                                            }
                                                        },
                                                    )

                                                    Sizer(width = 10)
                                                    Column(
                                                    ) {
                                                        Text(
                                                            orderItems.product.name,
                                                            fontFamily = General.satoshiFamily,
                                                            fontWeight = FontWeight.Medium,
                                                            fontSize = (16).sp,
                                                            color = CustomColor.neutralColor950,
                                                            textAlign = TextAlign.Center,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Sizer(width = 5)
                                                        orderItems.productVarient.forEach { value ->

                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {

                                                                Text(
                                                                    (value.varient_name
                                                                        ?: "") + ": ",
                                                                    fontFamily = General.satoshiFamily,
                                                                    fontWeight = FontWeight.Normal,
                                                                    fontSize = (16).sp,
                                                                    color = CustomColor.neutralColor950,
                                                                    textAlign = TextAlign.Center
                                                                )
                                                                when (value.varient_name == "Color") {
                                                                    true -> {
                                                                        val colorValue =
                                                                            General.convertColorToInt(
                                                                                value.product_varient_name
                                                                            )

                                                                        if (colorValue != null)
                                                                            Box(
                                                                                modifier = Modifier
                                                                                    .height(20.dp)
                                                                                    .width(20.dp)
                                                                                    .background(
                                                                                        colorValue,
                                                                                        RoundedCornerShape(
                                                                                            20.dp
                                                                                        )
                                                                                    )

                                                                                    .clip(
                                                                                        RoundedCornerShape(
                                                                                            20.dp
                                                                                        )
                                                                                    )
                                                                                //                                                    .padding(5.dp)
                                                                            )
                                                                    }

                                                                    else -> {
                                                                        Box(
                                                                            modifier = Modifier

                                                                                .clip(
                                                                                    RoundedCornerShape(
                                                                                        20.dp
                                                                                    )
                                                                                ),
                                                                            contentAlignment = Alignment.Center
                                                                        ) {
                                                                            Text(
                                                                                text = value.product_varient_name,
                                                                                fontFamily = General.satoshiFamily,
                                                                                fontWeight = FontWeight.Normal,
                                                                                fontSize = (16).sp,
                                                                                color = CustomColor.neutralColor800,
                                                                                textAlign = TextAlign.Center
                                                                            )
                                                                        }
                                                                    }
                                                                }

                                                            }
                                                        }
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
Text("Status : ",
                                                                fontFamily = General.satoshiFamily,
                                                                fontWeight = FontWeight.Normal,
                                                                fontSize = (16).sp,
                                                                color = CustomColor.neutralColor950,
                                                                textAlign = TextAlign.Center
                                                            )
                                                            Text(orderItems.orderItemStatus,
                                                                fontFamily = General.satoshiFamily,
                                                                fontWeight = FontWeight.Normal,
                                                                fontSize = (16).sp,
                                                                color = CustomColor.neutralColor800,
                                                                textAlign = TextAlign.Center
                                                            )
                                                           }


                                                    }

                                                }

                                                Sizer(5)
                                             Row(
                                                 modifier= Modifier.fillMaxWidth(),
                                                 horizontalArrangement = Arrangement.SpaceBetween,
                                                 verticalAlignment = Alignment.CenterVertically
                                             ){
                                                 Text(
                                                     "store Location",
                                                     fontFamily = General.satoshiFamily,
                                                     fontWeight = FontWeight.Bold,
                                                     fontSize = (24).sp,
                                                     color = CustomColor.neutralColor950,
                                                     textAlign = TextAlign.Center,
                                                     maxLines = 1,
                                                     overflow = TextOverflow.Ellipsis
                                                 )
                                                 IconButton({
                                                     requestPermssion.launch(
                                                         arrayOf(
                                                             Manifest.permission.ACCESS_FINE_LOCATION,
                                                             Manifest.permission.ACCESS_COARSE_LOCATION
                                                         )
                                                     )
                                                 }) {
                                                     Icon(
                                                         ImageVector.vectorResource(R.drawable.location_address_list),
                                                         "",
                                                         tint = CustomColor.primaryColor500
                                                     )
                                                 }

                                             }
                                            }


                                        }
//                                        Box(
//                                            modifier = Modifier
//                                                .padding(top = 5.dp)
//                                                .height(1.dp)
//                                                .fillMaxWidth()
//                                                .background(CustomColor.neutralColor200)
//                                        )
                                    }


                            }

                            Row(
                                modifier = Modifier
                                    .padding(top = 20.dp, bottom = 20.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        isExpanded.value = !isExpanded.value
                                    },
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Show Items")
                                Sizer(width = 5)
                                Icon(
                                    Icons.Default.KeyboardArrowDown, "",
                                    modifier = Modifier.rotate(roatation.value)
                                )
                            }


                            Box(
                                Modifier
                                    .padding(top = 10.dp)
                                    .width(((screenWidth) - 20).dp)
                            ) {
                                CustomBotton(

                                    buttonTitle = "Cencle Order",
                                    operation = {
                                        deletedId.value = order.id
                                        currutine.launch {
                                                isSendingData.value = true;
                                                val result = async {
                                                    homeViewModel.cencleOrder(order.id)
                                                }.await()
                                                isSendingData.value = false
                                                if (!result.isNullOrEmpty()) {
                                                    snackbarHostState
                                                        .showSnackbar(result)
                                                }
                                        }

                                    },
                                    color = CustomColor.alertColor_1_600,
//                                        isLoading = isSendingData.value && deletedId.value == order.id
                                )
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

