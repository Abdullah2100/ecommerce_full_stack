package com.example.e_commerc_delivery_man.ui.view.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.Util.General.reachedBottom
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.CustomBotton
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {

    var orders = homeViewModel.orders.collectAsState()
    var context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isSendingData = remember { mutableStateOf(false) }
    val deletedId = remember { mutableStateOf<UUID?>(null) }
    val coroutin = rememberCoroutineScope()


    val lazyState = rememberLazyListState()
    val reachedBottom = remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }
    var page = remember { mutableStateOf(1) }
    val isLoadingMore = remember { mutableStateOf(false) }

    val isRefresh = remember { mutableStateOf(false) }

    Log.d("loadingState",isLoadingMore.value.toString())
    LaunchedEffect(reachedBottom.value) {

        if(!orders.value.isNullOrEmpty() && reachedBottom.value){
            homeViewModel.getMyOrder(
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
            onRefresh = {homeViewModel.getMyOrder(mutableStateOf(1))}
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
                            order.order_items.forEach { value ->
                                Row {
                                    SubcomposeAsyncImage(
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(80.dp)
                                            .width(80.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        model = General.handlingImageForCoil(
                                            value.product.thmbnail,
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
                                        if (value.productVarient.isNotEmpty())
                                            Sizer(5)

                                        value.productVarient.forEach { varient ->

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {

                                                Text(
                                                    varient.varient_name + " :",
                                                    fontFamily = General.satoshiFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (16).sp,
                                                    color = CustomColor.neutralColor950,
                                                    textAlign = TextAlign.Center
                                                )
                                                Sizer(width = 5)
                                                when (varient.varient_name == "Color") {
                                                    true -> {
                                                        val colorValue =
                                                            General.convertColorToInt(varient.product_varient_name)

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
                                                                text = varient.product_varient_name,
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

                                    }
                                }
                            }
                            if (order.status == 1) {
                                Sizer(10)
                                CustomBotton(

                                    buttonTitle = "Cencle Order",
                                    operation = {
                                        deletedId.value = order.id
                                        coroutin.launch {
                                            isSendingData.value = true;
                                            val result = async {
                                                homeViewModel.deleteOrder(order.id)
                                            }.await()
                                            isSendingData.value = false
                                            var message = "Order deleted Seccesffuly"
                                            if (!result.isNullOrEmpty()) {
                                                message = result
                                            }
                                            snackbarHostState.showSnackbar(message)
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
        if(isSendingData.value)
            Dialog(
                onDismissRequest = {}
            ) {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(90.dp)
                        .background(Color.White,
                            RoundedCornerShape(15.dp))
                    , contentAlignment = Alignment.Center
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

