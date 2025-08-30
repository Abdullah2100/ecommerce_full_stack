package com.example.eccomerce_app.ui.view.account

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.OrderItemForMyStoreShape
import com.example.eccomerce_app.viewModel.OrderItemsViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderForMyStoreScreen(
    nav: NavHostController,
    userViewModel: UserViewModel,
    orderItemsViewModel: OrderItemsViewModel
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val orderData = orderItemsViewModel.orderItemForMyStore.collectAsState()
    val myInfo = userViewModel.userInfo.collectAsState()


    val lazyState = rememberLazyListState()
    val coroutineScop = rememberCoroutineScope()


    val page = remember { mutableIntStateOf(1) }


    val currentUpdateOrderItemId = remember { mutableStateOf<UUID?>(null) }

    val isSendingData = remember { mutableStateOf(false) }
    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }
    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }


    val snackBarHostState = remember { SnackbarHostState() }




    LaunchedEffect(reachedBottom.value) {
        if (!orderData.value.isNullOrEmpty() && reachedBottom.value) {
            Log.d("scrollReachToBottom", "true")
            orderItemsViewModel.getMyOrderItemBelongToMyStore(
                page,
                isLoadingMore
            )
        }

    }

    PullToRefreshBox(
        isRefreshing = isRefresh.value,
        onRefresh = {
            orderItemsViewModel.getMyOrderItemBelongToMyStore(

                mutableIntStateOf(1),
                null
            )
        },
    )
    {
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
                            "Order Belong To My Store",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = (24).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                nav.popBackStack()
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                "",
                                modifier = Modifier.size(30.dp),
                                tint = CustomColor.neutralColor950
                            )
                        }
                    },
                )
            },
        )
        {
            it.calculateTopPadding()
            it.calculateBottomPadding()

            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (orderData.value != null)
                    items(
                        items = orderData.value!!,
                        key = { order -> order.id }) { order ->

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 10.dp)
                        ) {
                            OrderItemForMyStoreShape(
                                orderItem = order,
                                context = context,
                                screenWidth = screenWidth

                            )
                            Sizer(10)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                when (order.orderItemStatus) {
                                    "Cancelled" -> {
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {

                                            CustomBotton(
                                                isEnable = true,
                                                operation = {},
                                                buttonTitle = "Cancelled",
                                                color = CustomColor.alertColor_1_600
                                            )
                                        }

                                    }

                                    "Excepted" -> {
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {

                                            CustomBotton(
                                                isEnable = true,
                                                operation = {},
                                                buttonTitle = "Excepted",
                                                color = CustomColor.alertColor_2_600
                                            )
                                        }

                                    }

                                    else -> {
                                        Box(
                                            modifier = Modifier.width(((screenWidth / 2) - 15).dp)
                                        )
                                        {

                                            CustomBotton(
                                                isLoading = isSendingData.value && currentUpdateOrderItemId.value == order.id,
                                                isEnable = !isSendingData.value,
                                                operation = {
                                                    coroutineScop.launch {
                                                        currentUpdateOrderItemId.value =
                                                            order.id
                                                        isSendingData.value = true
                                                        val result = async {
                                                            orderItemsViewModel.updateOrderItemStatusFromStore(
                                                                order.id,
                                                                0
                                                            )
                                                        }.await()
                                                        isSendingData.value = false
                                                        var message =
                                                            "Complete Update OrderItem Status"
                                                        if (!result.isNullOrEmpty()) {
                                                            message = result
                                                        }
                                                        snackBarHostState.showSnackbar(message)

                                                    }
                                                },

                                                buttonTitle = "Except",
                                                color = CustomColor.primaryColor700
                                            )
                                        }
                                        Box(
                                            modifier = Modifier.width(((screenWidth / 2) - 16).dp)
                                        )
                                        {

                                            CustomBotton(
                                                isLoading = isSendingData.value && currentUpdateOrderItemId.value == order.id,
                                                isEnable = !isSendingData.value,
                                                operation = {
                                                    coroutineScop.launch {
                                                        currentUpdateOrderItemId.value =
                                                            order.id
                                                        isSendingData.value = true
                                                        val result = async {
                                                            orderItemsViewModel.updateOrderItemStatusFromStore(
                                                                order.id,
                                                                1
                                                            )
                                                        }.await()
                                                        isSendingData.value = false

                                                        var message =
                                                            "Complete Update OrderItem Status"
                                                        if (!result.isNullOrEmpty()) {
                                                            message = result
                                                        }
                                                        snackBarHostState.showSnackbar(message)

                                                    }
                                                },
                                                buttonTitle = "Reject",
                                                color = CustomColor.alertColor_1_600
                                            )
                                        }
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
                    }
                }


                item {
                    Box(modifier = Modifier.height(50.dp))
                }
            }
        }


    }
}