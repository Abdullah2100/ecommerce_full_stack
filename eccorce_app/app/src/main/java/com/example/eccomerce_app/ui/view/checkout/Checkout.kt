package com.example.e_commercompose.ui.view.checkout

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.viewModel.HomeViewModel
import com.example.e_commercompose.R
import com.example.e_commercompose.model.PaymentMethodModel
import com.example.e_commercompose.ui.component.CustomBotton
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {
    val config = LocalConfiguration.current

    var cartData = homeViewModel.cartImes.collectAsState()
    var myInfo = homeViewModel.myInfo.collectAsState()
    var currentAddress = myInfo.value?.address?.firstOrNull { it.isCurrnt == true }
    val listOfPaymentMethod = listOf<PaymentMethodModel>(
        PaymentMethodModel("Cach", R.drawable.money, 1)
    )
    val slectedPaymentMethod = remember { mutableStateOf(0) }

    val coroutin = rememberCoroutineScope()
    val isSendingData = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val generalSetting = homeViewModel.generalSetting.collectAsState()
    val kiloPrice = generalSetting.value?.firstOrNull{it.name=="one_kilo_price"}?.value
    val distancToUser = homeViewModel.distance.collectAsState();

    val totalDeclivaryPrice = (distancToUser.value) *(kiloPrice?:0.0);
    Log.d("UserDistanc",distancToUser.value.toString())
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(end = 15.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        "Checkout",
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
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ){
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding()
                ){
                    CustomBotton(
                        isEnable = !isSendingData.value,
                        operation = {
                            coroutin.launch {
                                isSendingData.value=true;
                               val  result = async {
                                   homeViewModel.submitCartTitems()
                               }.await()
                                isSendingData.value= false
                                var message  = "Order Submit Seccesffuly"
                                if(!result.isNullOrEmpty())
                                {
                                    message = result
                                }
                                snackbarHostState.showSnackbar(message)
                                if(result.isNullOrEmpty()){
                                    nav.navigate(Screens.HomeGraph) {
                                        popUpTo(nav.graph.id) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        },
                        buttonTitle = "Place Order",
                        isLoading = isSendingData.value
                    )
                }
            }
        }

        )
    {
        it.calculateBottomPadding()
        it.calculateTopPadding()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 15.dp)
                .padding(
                    top = it.calculateTopPadding() + 20.dp, bottom = it.calculateBottomPadding()
                )

        ) {

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Delivery Address",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center

                            )
                            TextButton(onClick = {
                                nav.navigate(Screens.Address)
                            }) {
                                Text(
                                    "Change",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = CustomColor.neutralColor900,
                                    textAlign = TextAlign.Center,
                                    textDecoration = TextDecoration.Underline

                                )
                            }

                        }

                        Sizer(1)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.location_address_list),
                                "",
                                tint = CustomColor.neutralColor600
                            )
                            TextButton(onClick = {
                                nav.navigate(Screens.Address)
                            }) {
                                Text(
                                    currentAddress?.title ?: "",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.Center,
                                )
                            }

                        }
                    }


                }
                Sizer(10)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CustomColor.neutralColor200)
                )
                Sizer(25)

            }

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                    ) {

                        Text(
                            "Payment Method",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )

                        Sizer(15)

                        LazyRow() {
                            items(listOfPaymentMethod.size) { index ->
                                Row(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(((config.screenWidthDp - 30) / listOfPaymentMethod.size).dp)
                                        .border(
                                            if (slectedPaymentMethod.value == index) 0.dp else 1.dp,
                                            CustomColor.neutralColor200,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            if (slectedPaymentMethod.value == index) CustomColor.primaryColor700 else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clip(
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            slectedPaymentMethod.value = index
                                        },
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        ImageVector.vectorResource(listOfPaymentMethod[index].icon),
                                        "",
                                        tint = if (slectedPaymentMethod.value == index) Color.White else Color.Black
                                    )
                                    Sizer(width = 5)
                                    Text(
                                        listOfPaymentMethod[index].name,
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (slectedPaymentMethod.value == index) Color.White else CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Sizer(15)

                    }


                }
                Sizer(10)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CustomColor.neutralColor200)
                )
                Sizer(15)

            }

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            "Order Summary",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center

                        )


                        Sizer(15)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "\$${cartData.value.totalPrice}",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                        }
                        Sizer(15)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Delivery Fee",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "\$${(totalDeclivaryPrice).toString()}",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                        }
                        Sizer(15)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Distance To User In Kilo",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${distancToUser.value}",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                        }


                    }


                }

            }


        }


    }

}