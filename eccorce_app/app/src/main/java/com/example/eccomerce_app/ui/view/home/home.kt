package com.example.eccomerce_app.ui.view.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.ui.component.Sizer
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.HomeViewModel
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.ui.component.BannerBage
import com.example.eccomerce_app.ui.component.CategoryLoadingShape
import com.example.eccomerce_app.ui.component.CategoryShape
import com.example.eccomerce_app.ui.component.LocationLoadingShape

@Composable
fun HomePage(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {
    val configuration = LocalConfiguration.current

    var myInfo = homeViewModel.myInfo.collectAsState()
    var bannel = homeViewModel.homeBanners.collectAsState()
    var categories = homeViewModel.categories.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }
    val isClickingSearch = remember { mutableStateOf(false) }


    val sizeAnimation = animateDpAsState(
        if (!isClickingSearch.value) 80.dp else 0.dp,
    )

    LaunchedEffect(Unit) {
        homeViewModel.getMyInfo()
        homeViewModel.getCategories(1)
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 15.dp)
                .padding(top = 50.dp)

        ) {

            item {
                when (myInfo.value?.address == null) {
                    true -> {
                        LocationLoadingShape(configuration.screenWidthDp)
                    }

                    else -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.height(sizeAnimation.value)
                        ) {
                            Column(
                                modifier = Modifier
                                    .width((configuration.screenWidthDp - 30 - 34).dp)
                                    .clickable(
                                        enabled = true,
                                        interactionSource =interactionSource ,
                                        indication = null,
                                       onClick = {
                                           nav.navigate(Screens.HomeAddress)
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
                                    myInfo.value?.address?.firstOrNull { it.isCurrnt==true }?.title
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
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 10.dp, bottom = 5.dp)
                    ,horizontalArrangement = Arrangement.SpaceBetween) {
                    Text( "Category",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )
                    Text("View All",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )
                }

                when(categories.value==null){
                   true->{
                        CategoryLoadingShape(20)
                    }
                    else->{

                      when(categories.value!!.isEmpty()){
                          true->{}
                          else ->{
                              CategoryShape(categories.value!!)
                          }
                      }
                    }
                }
            }

            if(bannel.value!=null)
            item{
                BannerBage(
                    banners = bannel.value!!,
                    isMe = false,
                    nav=nav
                )
            }
        }


    }
}