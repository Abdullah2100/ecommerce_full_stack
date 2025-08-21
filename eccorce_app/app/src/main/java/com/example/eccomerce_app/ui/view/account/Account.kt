package com.example.e_commercompose.ui.view.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.AccountCustomBottom
import com.example.e_commercompose.ui.component.LogoutBotton
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.viewModel.OrderItemsViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPage(
    nav: NavHostController,
    userViewModel: UserViewModel,
    orderItemsViewModel: OrderItemsViewModel,
    authViewModel: AuthViewModel
) {

    val myInfo = userViewModel.userInfo.collectAsState()
    val storeId = myInfo.value?.storeId

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

            AccountCustomBottom(
                "Your Profile",
                R.drawable.user, {
                    nav.navigate(Screens.Profile)
                })
            AccountCustomBottom(
                "Address",
                R.drawable.location_address_list, {
                    nav.navigate(Screens.EditeOrAddNewAddress)
                })
            AccountCustomBottom(
                "Payment Me",
                R.drawable.credit_card,
                {}
            )
            AccountCustomBottom(
                "Notifications",
                R.drawable.notification_accout,
                {})
            AccountCustomBottom(
                "My Store",
                R.drawable.store, {
                    nav.navigate(
                        Screens.Store(
                            storeId?.toString(),
                            false
                        )
                    )
                })

            if (myInfo.value?.storeId != null)
                AccountCustomBottom("Order For My Store", R.drawable.order_belong_to_store, {
                    orderItemsViewModel.getMyOrderItemBelongToMyStore(
                        storeId = storeId ?: UUID.randomUUID(),
                        pageNumber = mutableIntStateOf(1),
                        isLoading = mutableStateOf(false)
                    )
                    nav.navigate(Screens.OrderForMyStore)
                })

            LogoutBotton("Logout", R.drawable.logout, {
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