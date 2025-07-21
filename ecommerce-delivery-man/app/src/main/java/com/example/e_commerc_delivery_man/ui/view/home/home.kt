package com.example.e_commerc_delivery_man.ui.view.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.room.util.TableInfo
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel
import com.example.e_commerc_delivery_man.Util.General.reachedBottom
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.component.MonayAnalys
import com.example.e_commerc_delivery_man.ui.component.OrdersAnalys
import com.example.e_commerc_delivery_man.ui.component.VerticalLine
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight", "SuspiciousIndentation")
@Composable
fun HomePage(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {
    val config =LocalConfiguration.current
    val sceenHeight = config.screenHeightDp
    val sceenWidth = config.screenWidthDp
    val requestPermssion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permission ->


        }
    )

    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }
    remember { mutableStateOf(false) }

    val myInfo = homeViewModel.myInfo.collectAsState()


    val snackbarHostState = remember { SnackbarHostState() }
    val coroutine = rememberCoroutineScope()


    val lazyState = rememberLazyListState()
    remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }
    remember { mutableStateOf(1) }


    LaunchedEffect(Unit) {
        homeViewModel.getMyInfo()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermssion.launch(input = android.Manifest.permission.POST_NOTIFICATIONS)
        }

    }





    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)

        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()


        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = { homeViewModel.getMyInfo(true) }
        ) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 15.dp)
                    .padding(top = 50.dp)

            ) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Image(
                                ImageVector.vectorResource(R.drawable.app_icon), "",
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                "DelivaryMan",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (40).sp,
                                color = CustomColor.primaryColor700,
                                textAlign = TextAlign.Start
                            )
                        }

                        Switch(
                            myInfo.value?.isAvaliable == true,
                            onCheckedChange = {
                                if (myInfo.value != null) {
                                    coroutine.launch {

                                        var result = async {

                                            homeViewModel.updateMyInfo(!myInfo.value!!.isAvaliable)
                                        }.await()
                                        if (!result.isNullOrEmpty()) {
                                            snackbarHostState.showSnackbar(result)
                                        }
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedBorderColor = Color.Transparent,
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CustomColor.primaryColor700,
                                disabledCheckedBorderColor = Color.Transparent,
                            )

                        )
                    }


                }


                item {
                    Sizer(30)
                    Text(
                        "Earing",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (20).sp,
                        color = Color.Black,
                        textAlign = TextAlign.Start
                    )
                    Sizer(10)
                    Column(
                        modifier = Modifier
//                            .height(240.dp)
                            .fillMaxWidth()
                            .background(
                                CustomColor.primaryColor500,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 20.dp)

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                ImageVector.vectorResource(R.drawable.wallet), "",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(100.dp)
                            )
                            Sizer(width = 14)
                            Column {
                                Text(
                                    "Banance",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (25).sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Start
                                )
                                Sizer(10)
                                Text(
                                    "${
                                        if ((myInfo.value?.analys?.dayFee ?: 0.0)
                                            - (myInfo.value?.analys?.weekFee ?: 0.0) < 0
                                        )
                                            ((myInfo.value?.analys?.dayFee ?: 0.0)
                                                    - (myInfo.value?.analys?.weekFee ?: 0.0)) * -1
                                        else (myInfo.value?.analys?.dayFee ?: 0.0)
                                                - (myInfo.value?.analys?.weekFee ?: 0.0)
                                    }",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (25).sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                       Sizer(30)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MonayAnalys(
                                myInfo.value?.analys?.dayFee?:
                                50000.0,"Today")
                            VerticalLine( width = 1, color = Color.White)
                            MonayAnalys(myInfo.value?.analys?.weekFee?:
                            53333333333.0,"Week")
                            VerticalLine( width = 1, color = Color.White)
                            MonayAnalys(myInfo.value?.analys?.monthFee?:
                            12355456789.0,"Month")


                        }

                    }
                }


                item {
                    Sizer(30)
                    Text(
                        "Order",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (20).sp,
                        color = Color.Black,
                        textAlign = TextAlign.Start
                    )
                    Sizer(10)
                    Row (
                        modifier = Modifier
//                            .height(240.dp)
                            .fillMaxWidth(),
//                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                   OrdersAnalys(myInfo.value?.analys?.dayOrder?:0,
                       "Todays",
                       "Ordes"
                       )

                        OrdersAnalys(myInfo.value?.analys?.weekOrder?:0,
                            "This Week",
                            "Ordes"
                        )

                    }
                }


                item {
                    Sizer(140)
                }
            }

        }
    }
}