package com.example.e_commercompose.ui.view.account

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.Util.General.reachedBottom
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.ui.view.home.SwappToDismiss
import com.example.e_commercompose.viewModel.HomeViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderForMyStoreScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
){
    var orderData  = homeViewModel.orderItemForMyStore.collectAsState()
    var myInfo  = homeViewModel.myInfo.collectAsState()

    var context = LocalContext.current
    var config = LocalConfiguration.current;
    var screenWidth = config.screenWidthDp
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUpdateOrderItme = remember { mutableStateOf<UUID?>(null) }
    val isSendingData = remember { mutableStateOf<Boolean>(false) }
    val coroutineScop = rememberCoroutineScope()

    Log.d("orderData",orderData.toString())

    val lazyState = rememberLazyListState()
    val reachedBottom = remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }
    var page = remember { mutableStateOf(1) }

    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }


    LaunchedEffect(reachedBottom.value) {
        if(!orderData.value.isNullOrEmpty() && reachedBottom.value){
            Log.d("scrollReachToBotton","true")
            homeViewModel.getMyOrderItemBelongToMyStore(
                store_id = myInfo.value?.store_id?: UUID.randomUUID(),
                page,
                isLoadingMore
            )
        }

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
                            Icons.Default.KeyboardArrowLeft,
                            "",
                            modifier = Modifier.size(30.dp),
                            tint = CustomColor.neutralColor950
                        )
                    }
                },
                )
        },
     ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()
        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                homeViewModel.getMyOrderItemBelongToMyStore(
                    store_id = myInfo.value!!.store_id!!,
                    mutableStateOf(1),
                    null
                )
            },
        )
        {
        LazyColumn(
            state = lazyState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orderData.value?.size?:0) { index ->

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                ,
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .wrapContentHeight()
                                        .width((screenWidth).dp),

                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SubcomposeAsyncImage(
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(80.dp)
                                            .width(80.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        model = General.handlingImageForCoil(
                                            orderData.value?.get(index)?.product?.thmbnail,
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
                                            orderData.value?.get(index)?.product?.name?:"",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = (16).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Sizer(width = 5)
                                        orderData.value?.get(index)?.productVarient?.forEach { value ->

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val title = value.varient_name
                                                Text(
                                                    title+" :",
                                                    fontFamily = General.satoshiFamily,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = (16).sp,
                                                    color = CustomColor.neutralColor950,
                                                    textAlign = TextAlign.Center
                                                )
                                                Sizer(width = 5)
                                                when (title == "Color") {
                                                    true -> {
                                                        val colorValue =
                                                            General.convertColorToInt(value.product_varient_name)

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

                                        Row {
                                            Text(
                                                "Quantity :",

                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = (16).sp,
                                                color = CustomColor.neutralColor950,
                                                textAlign = TextAlign.Center
                                            )
                                            Sizer(width = 5)
                                            Text(
                                                "${orderData.value?.get(index)?.quanity}",

                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = (16).sp,
                                                color = CustomColor.neutralColor950,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }

                                }




                            }

                            Sizer(10)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                when(orderData.value?.get(index)?.orderItemStatus){
                                    "Cancelled"->{
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                        ){

                                            CustomBotton(
                                                isEnable = true,
                                                operation = {},
                                                buttonTitle = "Cancelled",
                                                color = CustomColor.alertColor_1_600
                                            )
                                        }

                                    }
                                     "Excepted"->{
                                         Box(
                                             modifier = Modifier.fillMaxWidth()
                                         ){

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
                                            modifier = Modifier.width(((screenWidth/2)-10).dp)
                                        ){

                                            CustomBotton(
                                                isLoading = isSendingData.value&&currentUpdateOrderItme.value==orderData.value?.get(index)!!.id,
                                                isEnable = !isSendingData.value,
                                                operation = {
                                                    coroutineScop.launch {
                                                        currentUpdateOrderItme.value=orderData.value?.get(index)!!.id
                                                        isSendingData.value=true
                                                        var result = async {
                                                        homeViewModel.updateOrderItemStatusFromStore(orderData.value?.get(index)!!.id,0)
                                                        }.await()
                                                        isSendingData.value=false
                                                        var message = "Complate Update OrderItem Status";
                                                        if(!result.isNullOrEmpty())
                                                        {
                                                            message=result;
                                                        }
                                                        snackbarHostState.showSnackbar(message);

                                                    }
                                                },

                                                buttonTitle = "Except",
                                                color = CustomColor.primaryColor700
                                            )
                                        }
                                        Box(
                                            modifier = Modifier.width(((screenWidth/2)-10).dp)
                                        ){

                                            CustomBotton(
                                                isLoading = isSendingData.value&&currentUpdateOrderItme.value==orderData.value?.get(index)!!.id,
                                                isEnable = !isSendingData.value,
                                                operation = {
                                                    coroutineScop.launch {
                                                        currentUpdateOrderItme.value=orderData.value?.get(index)!!.id
                                                        isSendingData.value=true
                                                        var result = async {
                                                            homeViewModel.updateOrderItemStatusFromStore(orderData.value?.get(index)!!.id,1)
                                                        }.await()
                                                        isSendingData.value=false

                                                        var message = "Complate Update OrderItem Status";
                                                        if(!result.isNullOrEmpty())
                                                        {
                                                            message=result;
                                                        }
                                                        snackbarHostState.showSnackbar(message);

                                                    }
                                                },
                                                buttonTitle = "Reject",
                                                color = CustomColor.alertColor_1_600
                                            )
                                        }
                                    }
                                }

                            }

                            if(index+1!=orderData.value?.size){
                                Sizer(10)
                                Box(
                                    modifier = Modifier
                                        .padding(top = 5.dp)
                                        .height(1.dp)
                                        .fillMaxWidth()
                                        .background(CustomColor.neutralColor200)
                                )
                            }

                        }


            }

            if (isLoadingMore.value) {
                item {
                    Box(modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth(),
                        contentAlignment = Alignment.Center)
                    {
                        CircularProgressIndicator(color = CustomColor.primaryColor700)
                    }
                }
            }


            item{
                Box(modifier = Modifier.height(50.dp))
            }
        }}


    }
}