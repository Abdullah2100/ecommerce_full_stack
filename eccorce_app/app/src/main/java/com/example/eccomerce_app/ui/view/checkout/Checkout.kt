package com.example.eccomerce_app.ui.view.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.ui.component.BannerBage
import com.example.eccomerce_app.ui.component.CategoryLoadingShape
import com.example.eccomerce_app.ui.component.CategoryShape
import com.example.eccomerce_app.ui.component.LocationLoadingShape
import com.example.eccomerce_app.ui.component.ProductLoading
import com.example.eccomerce_app.ui.component.ProductShape
import com.example.eccomerce_app.ui.component.Sizer
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {

    var myInfo = homeViewModel.myInfo.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(end = 15.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        "Product Detail",
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

        )
    {
        it.calculateBottomPadding()
        it.calculateTopPadding()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 15.dp)
                .padding(top = it.calculateTopPadding()+20.dp
                , bottom = it.calculateBottomPadding())

        ) {

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            color = CustomColor.neutralColor200,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
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

                   IconButton(
                       onClick = {
                           nav.navigate(Screens.Address)
                       }
                   ) {
                       Icon(
                           Icons.Outlined.Edit,
                           "",
                           tint = CustomColor.neutralColor950,
                           modifier = Modifier.size(30.dp)

                       )
                   }
                }
            }
        }



    }

}