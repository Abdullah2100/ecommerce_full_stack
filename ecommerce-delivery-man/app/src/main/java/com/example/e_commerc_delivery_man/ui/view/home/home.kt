package com.example.e_commerc_delivery_man.ui.view.home

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.util.General
import com.example.e_commerc_delivery_man.ui.component.MonayAnalys
import com.example.e_commerc_delivery_man.ui.component.OrdersAnalys
import com.example.e_commerc_delivery_man.ui.component.VerticalLine
import com.example.e_commerc_delivery_man.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight", "SuspiciousIndentation")
@Composable
fun HomePage(
    nav: NavHostController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current

    val coroutine = rememberCoroutineScope()
    val lazyState = rememberLazyListState()
    val state = rememberPullToRefreshState()


    val myInfo = userViewModel.myInfo.collectAsState()



    val isRefresh = remember { mutableStateOf(false) }


    val snackBarHostState = remember { SnackbarHostState() }


    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permission ->
            if (!permission) {
                finishAffinity(context as Activity)  // Close all activities
                exitProcess(0)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(input = android.Manifest.permission.POST_NOTIFICATIONS)
        }

    }





    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                actions = {
                    Switch(
                        myInfo.value?.isAvailable == true,
                        onCheckedChange = {
                            if (myInfo.value != null) {
                                coroutine.launch {

                                    val result = async {
                                        userViewModel.updateDeliveryStatus(!myInfo.value!!.isAvailable)
                                    }.await()
                                    if (!result.isNullOrEmpty()) {
                                        snackBarHostState.showSnackbar(result)
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
                },
                navigationIcon = {
                    Image(
                        ImageVector.vectorResource(R.drawable.app_icon), "",
                        modifier = Modifier.size(50.dp)
                    )
                },

                title = {
                    Text(
                        "Delivery Man",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (30).sp,
                        color = CustomColor.primaryColor700,
                        textAlign = TextAlign.Start
                    )
                }
                ,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()


        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                coroutine.launch {
                    if (!isRefresh.value) isRefresh.value = true
                    userViewModel.getMyInfo(true)
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
                        .padding(top = 50.dp)
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
                    .background(Color.White)
                    .padding(horizontal = 15.dp)
                    .padding(top = 50.dp)

            ) {


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
                                myInfo.value?.analys?.dayFee ?: 50000.0, "Today"
                            )
                            VerticalLine(width = 1, color = Color.White)
                            MonayAnalys(
                                myInfo.value?.analys?.weekFee ?: 53333333333.0, "Week"
                            )
                            VerticalLine(width = 1, color = Color.White)
                            MonayAnalys(
                                myInfo.value?.analys?.monthFee ?: 12355456789.0, "Month"
                            )


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
                    Row(
                        modifier = Modifier
//                            .height(240.dp)
                            .fillMaxWidth(),
//                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        OrdersAnalys(
                            myInfo.value?.analys?.dayOrder ?: 0,
                            "Todays",
                            "Ordes"
                        )

                        OrdersAnalys(
                            myInfo.value?.analys?.weekOrder ?: 0,
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