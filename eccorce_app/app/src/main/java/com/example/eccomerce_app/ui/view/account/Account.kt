package com.example.e_commercompose.ui.view.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.AccountCustomBottom
import com.example.e_commercompose.ui.component.LogoutBotton
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.viewModel.HomeViewModel
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPage(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {

    var myInfo = homeViewModel.myInfo.collectAsState()
    var storeId = myInfo.value?.store_id
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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
                        "Account",
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
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = it.calculateTopPadding() + 20.dp)
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            AccountCustomBottom("Your Profile", R.drawable.user, {
                nav.navigate(Screens.Profile)
            })
            AccountCustomBottom("Locations", R.drawable.location_address_list, {
                nav.navigate(Screens.Address)
            })
//           AccountCustomBottom("My Order", R.drawable.order, {})
            AccountCustomBottom("Payment Me", R.drawable.credit_card, {})
            AccountCustomBottom("Notifications", R.drawable.notification_accout, {})
            AccountCustomBottom("My Store", R.drawable.store, {
//                if (myInfo.value != null)
//                    homeViewModel.kgetProducts(store_id = myInfo.value!!.store_id, pageNumber = 1)
                nav.navigate(
                    Screens.Store(
                        if (storeId == null) null else storeId.toString(),
                        false
                    )
                )
            })

            if (myInfo.value?.store_id != null)
                AccountCustomBottom("Order For My Store", R.drawable.order_belong_to_store, {
                    homeViewModel.getMyOrderItemBelongToMyStore(
                        store_id = storeId?: UUID.randomUUID(),
                        pageNumber=mutableStateOf(1),
                        isLoading= mutableStateOf(false)
                        )
                    nav.navigate(Screens.OrderForMyStore)
                })
            LogoutBotton("Logout", R.drawable.logout, {
                homeViewModel.logout()
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