package com.example.eccomerce_app.ui.view.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.eccomerce_app.viewModel.OrderViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(orderViewModel: OrderViewModel) {

    val context = LocalContext.current

    val coroutine = rememberCoroutineScope()
    val lazyState = rememberLazyListState()

    val orders = orderViewModel.orders.collectAsState()

    val isSendingData = remember { mutableStateOf(false) }
    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }
    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }

    val deletedId = remember { mutableStateOf<UUID?>(null) }

    val snackBarHostState = remember { SnackbarHostState() }


    val page = remember { mutableIntStateOf(1) }

    Log.d("loadingState", isLoadingMore.value.toString())
    LaunchedEffect(reachedBottom.value) {

        if (!orders.value.isNullOrEmpty() && reachedBottom.value) {
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
                        "My Order",
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
        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = { orderViewModel.getMyOrders(mutableIntStateOf(1)) }
        ) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    orders.value?.forEach { order ->
                        Log.d("imageUrl", order.toString())

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                                .border(
                                    1.dp,
                                    CustomColor.neutralColor200,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 10.dp)

                        ) {
                            order.orderItems.forEach { value ->
                                Row {
                                    SubcomposeAsyncImage(
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(80.dp)
                                            .width(80.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        model = General.handlingImageForCoil(
                                            value.product.thumbnail,
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

                                    Sizer(width = 5)

                                    Column {
                                        Text(
                                            value.product.name,
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = (16).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Start
                                        )
                                        if (value.productVariant.isNotEmpty())
                                            Sizer(5)

                                        value.productVariant.forEach { variant ->

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {

                                                Text(
                                                    variant.variantName + " :",
                                                    fontFamily = General.satoshiFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (16).sp,
                                                    color = CustomColor.neutralColor950,
                                                    textAlign = TextAlign.Center
                                                )
                                                Sizer(width = 5)
                                                when (variant.variantName == "Color") {
                                                    true -> {
                                                        val colorValue =
                                                            General.convertColorToInt(variant.productVariantName)

                                                        if (colorValue != null)
                                                            Box(
                                                                modifier = Modifier
                                                                    .height(20.dp)
                                                                    .width(20.dp)
                                                                    .background(
                                                                        colorValue,
                                                                        RoundedCornerShape(20.dp)
                                                                    )

                                                                    .clip(RoundedCornerShape(20.dp))
//                                                    .padding(5.dp)
                                                            )
                                                    }

                                                    else -> {
                                                        Box(
                                                            modifier = Modifier

                                                                .clip(RoundedCornerShape(20.dp)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = variant.productVariantName,
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

                                            Text(
                                                "Status  :",
                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = (16).sp,
                                                color = CustomColor.neutralColor950,
                                                textAlign = TextAlign.Center
                                            )
                                            Sizer(width = 5)
                                            Text(
                                                text = value.orderItemStatus,
                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = (16).sp,
                                                color = when (value.orderItemStatus) {
                                                    "InProgress" -> CustomColor.alertColor_3_500
                                                    "Excepted" -> CustomColor.alertColor_2_700
                                                    else -> CustomColor.alertColor_2_700
                                                },
                                                textAlign = TextAlign.Center
                                            )


                                        }
                                    }
                                }

                                Sizer(5)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Total Price",
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "$${order.totalPrice}",

                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Sizer(5)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "DeliveryFee Price",
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "$${order.deliveryFee}",

                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Sizer(5)

                                CustomBotton(

                                    buttonTitle = "Cancel Order",
                                    operation = {
                                        deletedId.value = order.id
                                        coroutine.launch {

                                            isSendingData.value = true

                                            val result = async {
                                                orderViewModel.deleteOrder(order.id)
                                            }.await()

                                            isSendingData.value = false
                                            val message = result ?: "Order deleted Successfully"

                                            snackBarHostState.showSnackbar(message)
                                        }

                                    },
                                    color = CustomColor.alertColor_1_600,
                                    isLoading = isSendingData.value && deletedId.value == order.id
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
        if (isSendingData.value)
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

