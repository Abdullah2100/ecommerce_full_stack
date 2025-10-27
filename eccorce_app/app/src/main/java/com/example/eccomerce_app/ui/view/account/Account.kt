package com.example.e_commercompose.ui.view.account

import android.R.attr.layoutDirection
import android.annotation.SuppressLint
import android.app.LocaleConfig
import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.AccountCustomBottom
import com.example.e_commercompose.ui.component.LogoutBotton
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General.currentLocal
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.viewModel.OrderItemsViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPage(
    nav: NavHostController,
    userViewModel: UserViewModel,
    orderItemsViewModel: OrderItemsViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutine = rememberCoroutineScope()

    val width = config.screenWidthDp

    val myInfo = userViewModel.userInfo.collectAsState()
    val currentLocale = currentLocal.collectAsState()


    val storeId = myInfo.value?.storeId


    val isChangingLanguage = remember { mutableStateOf(false) }


   val updateDirection = remember {
       derivedStateOf {
           if (currentLocale.value == "ar") {
               LayoutDirection.Rtl

           } else {
               LayoutDirection.Ltr
           }
       }
   }


    fun whenIsDoneDo(locale: String, context: Context) {
        val locale = Locale(locale)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }


    CompositionLocalProvider(
        LocalLayoutDirection provides updateDirection.value
    ) {
        Scaffold(

            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    ),
                    title = {
                        Text(
                            stringResource(R.string.account),
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
                    scrollBehavior = scrollBehavior
                )
            }
        )
        {
            it.calculateTopPadding()
            it.calculateBottomPadding()

            if (isChangingLanguage.value) Dialog(
                onDismissRequest = {})
            {
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


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(top = it.calculateTopPadding() + 20.dp)
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                AccountCustomBottom(
                    stringResource(R.string.your_profile),
                    R.drawable.user, {
                        nav.navigate(Screens.Profile)
                    })
                AccountCustomBottom(
                    stringResource(R.string.address),
                    R.drawable.location_address_list, {
                        nav.navigate(Screens.EditeOrAddNewAddress)
                    })
                /*AccountCustomBottom(
                    stringResource(R.string.payment_me),
                    R.drawable.credit_card,
                    {}
                )
                AccountCustomBottom(
                    stringResource(R.string.notifications),
                    R.drawable.notification_accout,
                    {})
                */

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                    OutlinedButton(
                        modifier = Modifier.width((width / 2 - 23).dp),
                        onClick = {
                            coroutine.launch {
                                isChangingLanguage.value = true
                                delay(100)
                                async { userViewModel.updateCurrentLocale("ar") }.await()
                                currentLocal.emit("ar")
                                whenIsDoneDo("ar",context)

                                isChangingLanguage.value = false
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Arabic",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = if (currentLocale.value == "ar"
                                || currentLocale.value==null) CustomColor.neutralColor950
                            else CustomColor.neutralColor200,
                            textAlign = TextAlign.Center

                        )
                    }

                    OutlinedButton(
                        modifier = Modifier.width((width / 2 - 23).dp),
                        onClick = {
                            coroutine.launch {
                                isChangingLanguage.value = true
                                delay(100)
                                async { userViewModel.updateCurrentLocale("en") }.await()
                                currentLocal.emit("en")
                                whenIsDoneDo("en",context)
                                isChangingLanguage.value = false
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "English",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = if (currentLocale.value == "en") CustomColor.neutralColor950
                            else CustomColor.neutralColor200,
                            textAlign = TextAlign.Center

                        )

                    }
                }
                Box(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(CustomColor.neutralColor200)
                )


                AccountCustomBottom(
                    stringResource(R.string.my_store),
                    R.drawable.store, {
                        nav.navigate(
                            Screens.Store(
                                storeId?.toString(),
                                false
                            )
                        )
                    })

                if (myInfo.value?.storeId != null)
                    AccountCustomBottom(
                        stringResource(R.string.order_for_my_store),
                        R.drawable.order_belong_to_store,
                        {
                            orderItemsViewModel.getMyOrderItemBelongToMyStore(
                                pageNumber = mutableIntStateOf(1),
                                isLoading = mutableStateOf(false)
                            )
                            nav.navigate(Screens.OrderForMyStore)
                        })

                LogoutBotton(stringResource(R.string.logout), R.drawable.logout, {
                    authViewModel.logout()
                    nav.navigate(Screens.AuthGraph)
                    {
                        popUpTo(nav.graph.id) {
                            inclusive = true
                        }
                    }
                })

            }
        }
    }

}