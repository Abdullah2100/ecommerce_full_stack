package com.example.eccomerce_app.ui.view.location

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.eccomerce_app.R
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.ui.component.Sizer
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.HomeViewModel
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsList(
    nav: NavHostController,
    homeViewModle: HomeViewModel,
    isFromHome: Boolean?=false
) {
    val fontScall = LocalDensity.current.fontScale
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val locationss = homeViewModle.locations.collectAsState()
    val isLoading = homeViewModle.isLoading.collectAsState()
    val currentLocationId = remember { mutableStateOf<UUID?>(null) }
    val isPressLocation = remember { mutableStateOf<Boolean>(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
,
                modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            CenterAlignedTopAppBar(

                title = {
                    Text(
                        "Enter Your Location",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (24 / fontScall).sp,
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

        when (locationss.value.isNullOrEmpty()) {
            true -> {
                Column(modifier=Modifier
                    .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                    ){

                        Icon(
                            imageVector = ImageVector
                                .vectorResource(R.drawable.location_arrow), contentDescription = "",
                            tint = CustomColor.primaryColor700,
                            modifier = Modifier.size(40.dp)
                        )

                    Sizer(10)
                    Text(
                        "There is No Locations Found",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (20 / fontScall).sp,
                        color = CustomColor.primaryColor950,
                        textAlign = TextAlign.Center

                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .padding(horizontal = 15.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {

                    items(locationss.value!!.size) {index->
                       Column(
                           modifier = Modifier
                               .clip(RoundedCornerShape(8.dp))
                               .clickable{
                                   isPressLocation.value=true
                                   currentLocationId.value=locationss.value!![index].id
                               }
                       ) {
                           Row(
                               modifier = Modifier
                                   .padding(top=9.dp, start = 4.dp)
                                   .fillMaxWidth(),
                               verticalAlignment = Alignment.CenterVertically
                           ){
                               Icon(
                                   imageVector = ImageVector
                                       .vectorResource(R.drawable.location_arrow), contentDescription = "",
                                   tint = CustomColor.primaryColor700,
                                   modifier = Modifier.size(24.dp)
                               )
                               Sizer(width = 20)
                               Text(
                                   locationss.value!![index].title,
                                   fontFamily = General.satoshiFamily,
                                   fontWeight = FontWeight.Medium,
                                   fontSize = (16 / fontScall).sp,
                                   color = CustomColor.primaryColor950,
                                   textAlign = TextAlign.Center

                               )
                           }
                           Sizer(20)
                           Box(
                               modifier = Modifier.fillParentMaxWidth()
                                   .height(1.dp)
                                   .background(CustomColor.neutralColor200)
                           )
                       }
                    }
                }

                if(isPressLocation.value)
                {
                    AlertDialog(
                        onDismissRequest = {
                            //Logic when dismiss happens
//                            isPressLocation.value=false
                        },

                        text = {

                            Text("Make this location as active Location",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16 / fontScall).sp,
                                color = CustomColor.primaryColor950,
                                textAlign = TextAlign.Center

                            )
                               },
                        confirmButton = {
                        TextButton(onClick = {
                            isPressLocation.value=false
                            homeViewModle.setCurrentActiveUserAddress(
                                addressId = currentLocationId.value!!,
                                snackBark = snackbarHostState,
                                nav = nav,
                                isFromLocationHome = isFromHome
                            )
                        }) {
                            Text("okay",
                                fontFamily = General.satoshiFamily,
                              fontWeight = FontWeight.Normal,
                                fontSize = (16 / fontScall).sp,
                                color = CustomColor.primaryColor950,
                                textAlign = TextAlign.Center

                            )
                        }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                isPressLocation.value=false
                            }) {

                                Text("Deny",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = (16 / fontScall).sp,
                                    color = CustomColor.primaryColor950,
                                    textAlign = TextAlign.Center)
                            }
                        })
                }

                if(isLoading.value)
                    Dialog(
                        onDismissRequest = {}
                    ) {
                        Box(
                            modifier = Modifier
                                .height(90.dp)
                                .width(90.dp)
                                .background(Color.White,
                                    RoundedCornerShape(15.dp))
                            , contentAlignment = Alignment.Center
                        )
                        {
                            CircularProgressIndicator(
                                color = CustomColor.primaryColor700,
                            modifier = Modifier.size(40.dp)
                            )
                        }
                    }
            }
        }

    }
}