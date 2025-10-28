package com.example.e_commercompose.ui.view.account

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.e_commercompose.ui.component.LogoutButton
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General.currentLocal
import com.example.eccomerce_app.util.General.whenLanguageUpdateDo
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.viewModel.OrderItemsViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutine = rememberCoroutineScope()

    val myInfo = userViewModel.userInfo.collectAsState()
    val currentLocale = currentLocal.collectAsState()


    val storeId = myInfo.value?.storeId


    val isChangingLanguage = remember { mutableStateOf(false) }
    val isExpandLanguage = remember { mutableStateOf(false) }


    val updateDirection = remember {
        derivedStateOf {
            if (currentLocale.value == "ar") {
                LayoutDirection.Rtl

            } else {
                LayoutDirection.Ltr
            }
        }
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
                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(CustomColor.neutralColor200)
                )

                AccountCustomBottom(
                    stringResource(R.string.address),
                    R.drawable.location_address_list, {
                        nav.navigate(Screens.EditeOrAddNewAddress)
                    })
                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(CustomColor.neutralColor200)
                )

                AccountCustomBottom(
                    "Language",
                    R.drawable.language,
                    additionalComponent = {

                        Box {
                            TextButton(
                                modifier = Modifier
                                    .offset(y = 2.dp),
                                onClick = { isExpandLanguage.value = true }) {
                                Text(
                                    if (currentLocale.value == "en") "English" else "Arabic",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.Center

                                )
                            }
                            DropdownMenu(
                                containerColor = Color.White,
                                expanded = isExpandLanguage.value,
                                onDismissRequest = { isExpandLanguage.value = false }) {
                                listOf<String>(
                                    "العربية",
                                    "English"
                                ).forEach { lang ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                lang,
                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 18.sp,
                                                color = CustomColor.neutralColor950,
                                                textAlign = TextAlign.Center

                                            )
                                        },
                                        onClick = {
                                            coroutine.launch {
                                                isChangingLanguage.value = true
                                                delay(100)
                                                val currentLange =
                                                    if (lang == "العربية") {
                                                        if (currentLocale.value == "en")
                                                            "ar"
                                                        else ""
                                                    } else {
                                                        if (currentLocale.value == "ar")
                                                            "en"
                                                        else ""
                                                    }
                                                if (currentLange.isEmpty()) {
                                                    isChangingLanguage.value = false
                                                    return@launch
                                                };
                                                async {
                                                    userViewModel.updateCurrentLocale(
                                                        currentLange
                                                    )
                                                }.await()
                                                currentLocal.emit(currentLange)
                                                whenLanguageUpdateDo(currentLange, context)
                                                isChangingLanguage.value = false
                                                isExpandLanguage.value = false

                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                )

                HorizontalDivider(
                    modifier = Modifier
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

                HorizontalDivider(
                    modifier = Modifier
//                        .padding(top = 5.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(CustomColor.neutralColor200)
                )
                if (myInfo.value?.storeId != null) {

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


                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(CustomColor.neutralColor200)
                    )
                }
                LogoutButton(stringResource(R.string.logout), R.drawable.logout, {
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