package com.example.e_commercompose.ui.view.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.viewModel.HomeViewModel
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.BannerBage
import com.example.e_commercompose.ui.component.CategoryLoadingShape
import com.example.e_commercompose.ui.component.CategoryShape
import com.example.e_commercompose.ui.component.LocationLoadingShape
import com.example.e_commercompose.ui.component.ProductLoading
import com.example.e_commercompose.ui.component.ProductShape
import com.example.e_commercompose.Util.General.reachedBottom
import com.example.e_commercompose.ui.component.BannerLoading
import kotlin.collections.isNullOrEmpty

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight", "SuspiciousIndentation")
@Composable
fun HomePage(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {
    val configuration = LocalConfiguration.current
    val lazyState = rememberLazyListState()


    val myInfo = homeViewModel.myInfo.collectAsState()

    val banner = homeViewModel.bannersRadmom.collectAsState()

    val categories = homeViewModel.categories.collectAsState()

    val products = homeViewModel.products.collectAsState()

    val interactionSource = remember { MutableInteractionSource() }

    val isClickingSearch = remember { mutableStateOf(false) }

    val requestPermssion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permission ->


        }
    )

    val page = remember { mutableIntStateOf(1) }

    val sizeAnimation = animateDpAsState(if (!isClickingSearch.value) 80.dp else 0.dp)

    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }

    val reachedBottom = remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }



    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermssion.launch(input = android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    LaunchedEffect(reachedBottom.value) {
        if (!products.value.isNullOrEmpty() && reachedBottom.value) {
            Log.d("scrollReachToBottom", "true")
            homeViewModel.getProducts(
                page,
                isLoadingMore
            )
        }

    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()


        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                homeViewModel.initialFun()
                isRefresh.value = false;
            }
        ) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 15.dp)
                    .padding(top = 50.dp)

            ) {

                //address info
                item {
                    when (myInfo.value?.address == null && categories.value == null) {
                        true -> {
                            LocationLoadingShape((configuration.screenWidthDp))
                        }

                        else -> {
                            if (!myInfo.value?.address.isNullOrEmpty())
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.height(sizeAnimation.value)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .width(width = (configuration.screenWidthDp - 30 - 34).dp)
                                            .clickable(
                                                enabled = true,
                                                interactionSource = interactionSource,
                                                indication = null,
                                                onClick = {
                                                    nav.navigate(Screens.Address)
                                                }
                                            )
                                    ) {
                                        Text(
                                            "Loaction",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor800,
                                            textAlign = TextAlign.Center

                                        )
                                        Sizer(1)
                                        Text(
                                            myInfo.value?.address?.firstOrNull { it.isCurrnt == true }?.title
                                                ?: "",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 18.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )
                                    }

                                    Icon(
                                        Icons.Outlined.Notifications,
                                        "",
                                        tint = CustomColor.neutralColor950,
                                        modifier = Modifier.size(30.dp)

                                    )
                                }
                        }
                    }


                }

                //this the search box
                if (!products.value.isNullOrEmpty())
                    item {
                        Card(
                            modifier = Modifier
                                .padding(top = 5.dp, bottom = 10.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = {
                                        isClickingSearch.value = !isClickingSearch.value
                                    }
                                ), colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = 5.dp,
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 15.dp, bottom = 15.dp, start = 4.dp)

                            ) {

                                Icon(
                                    Icons.Outlined.Search, "",
                                    tint = CustomColor.neutralColor950,
                                    modifier = Modifier.size(24.dp)
                                )
                                Sizer(width = 5)
                                Text(
                                    "Find your favorite items",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = CustomColor.neutralColor800,
                                    textAlign = TextAlign.Center

                                )

                            }

                        }
                    }


                item {
                    when (categories.value == null) {
                        true -> {
                            CategoryLoadingShape()
                        }

                        else -> {
                            when (categories.value!!.isEmpty()) {
                                true -> {}
                                else -> {

                                    CategoryShape(
                                        categories.value!!.take(4),
                                        homeViewModel,
                                        nav
                                    )
                                }
                            }
                        }
                    }
                }

                //banner section
                item {
                    when (banner.value == null) {
                        true -> {
                            BannerLoading()
                        }

                        else -> {
                            if (!banner.value.isNullOrEmpty())
                                BannerBage(
                                    banners = banner.value!!,
                                    isMe = false,
                                    nav = nav
                                )
                        }
                    }


                }


                //product

                item {

                    Sizer(10)
                    when (products.value == null) {
                        true -> {
                            ProductLoading()
                        }

                        else -> {
                            if (products.value!!.isNotEmpty()) {
                                ProductShape(products.value!!, nav = nav)
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
                    Sizer(140)
                }
            }

        }
    }
}