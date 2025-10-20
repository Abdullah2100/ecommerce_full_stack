package com.example.e_commerc_delivery_man.ui.view.account

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.util.General
import com.example.e_commerc_delivery_man.model.enMapType
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.AccountCustomBottom
import com.example.e_commerc_delivery_man.ui.component.LogoutBotton
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.AuthViewModel
import com.example.e_commerc_delivery_man.viewModel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPage(
    nav: NavHostController,
    userModelView: UserViewModel,
    authViewModel: AuthViewModel
) {

    val myInfo = userModelView.myInfo.collectAsState()

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

            AccountCustomBottom("Your Profile", R.drawable.user, {
                nav.navigate(Screens.Profile)
            })
            AccountCustomBottom("Locations", R.drawable.location_address_list, {
                if (myInfo.value != null)
                    nav.navigate(
                        Screens.Map(
                            id = null,
                            title = "my Place",
                            lognit = myInfo.value!!.address.longitude,
                            latitt = myInfo.value!!.address.latitude,
                            isFromLogin = false,
                            mapType = enMapType.My
                        )
                    )
            })
//           AccountCustomBottom("My Order", R.drawable.order, {})
            AccountCustomBottom("Payment Me", R.drawable.credit_card, {})
            AccountCustomBottom("Notifications", R.drawable.notification_accout, {})



            LogoutBotton("Logout", R.drawable.logout, {
                authViewModel.logout()
                nav.navigate(Screens.Login)
                {
                    popUpTo(nav.graph.id) {
                        inclusive = true
                    }
                }
            })

        }
    }

}