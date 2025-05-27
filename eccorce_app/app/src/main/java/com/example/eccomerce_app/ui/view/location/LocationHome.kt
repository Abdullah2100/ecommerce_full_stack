package com.example.eccomerce_app.ui.view.location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.HomeViewModel
import com.example.eccomerce_app.R
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.dto.request.AddressRequestDto
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.ui.component.CustomBotton
import com.example.eccomerce_app.ui.component.CustomTitleBotton
import com.example.eccomerce_app.ui.component.Sizer
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationHomeScreen(
    nav: NavHostController,
    homeViewModle: HomeViewModel,
    ) {
    val context = LocalContext.current;
    val fontScall = LocalDensity.current.fontScale

    val isLoading = homeViewModle.isLoading.collectAsState()

    val currutine = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    val isShownLocationTitleDialog = remember { mutableStateOf(false) }
    val isNotEnablePermission = remember { mutableStateOf(false) }
    val locationTitle = remember { mutableStateOf(TextFieldValue()) }


    val locationHolder = remember { mutableStateOf<AddressRequestDto?>(null) }

    val isPressAddNewAddress = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val requestPermssion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permission ->
            val arePermissionsGranted = permission.values.reduce { acc, next ->
                acc && next
            }
            if (arePermissionsGranted) {
                currutine.launch(Dispatchers.Main) {

                    try {
                        isPressAddNewAddress.value=true

                            val data = fusedLocationClient.lastLocation.await()
                            data?.let {
                                    location->

                               locationHolder.value = AddressRequestDto(
                                   longitude = location.longitude,
                                   latitude = location.latitude,
                                   title = "home")
                            }


                    } catch (e: SecurityException) {
                      var   error = "Permission exception: ${e.message}"
                    }
                    finally {
                        isShownLocationTitleDialog.value = true;

                    }
                }
            } else {
                isNotEnablePermission.value=true
            }
        }
    )

    LaunchedEffect(Unit) {
        homeViewModle.getMyInfo()
    }

    Scaffold(
        bottomBar = {
            if(isPressAddNewAddress.value)
                ModalBottomSheet(
                    onDismissRequest = {
                        isPressAddNewAddress.value = false
                    },
                    sheetState = sheetState
                ) {

                    Column(modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()) {

                        Row(modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(2.dp))
                                .clickable{
                                   currutine.launch {
                                       isPressAddNewAddress.value=false
                                       var result = async {
                                           homeViewModle.addUserAddress(
                                               longit = locationHolder.value?.longitude,
                                               latit = locationHolder.value?.latitude,
                                               title = "home")
                                       }.await()

                                       if(result==null){
                                           snackbarHostState.showSnackbar("complate adding address")
                                           nav.navigate(Screens.HomeGraph) {
                                               popUpTo(nav.graph.id) {
                                                   inclusive = true
                                               }
                                           }
                                       }
                                       else{
                                           snackbarHostState.showSnackbar(result)
                                       }
                                   }
                                },
                            horizontalArrangement = Arrangement.Start) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                "",
                                tint = CustomColor.neutralColor950,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Current Address",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = CustomColor.neutralColor950,
                            )
                        }

                        Sizer(20)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.outline_map),
                                "",
                                tint = CustomColor.neutralColor950,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Chose From Map",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = CustomColor.neutralColor950,
                            )
                        }


                    }
                }

        },

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .background(Color.White)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .background(
                        CustomColor.primaryColor50,
                        RoundedCornerShape(40.dp),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector
                        .vectorResource(R.drawable.location), contentDescription = "",
                    tint = CustomColor.primaryColor700
                )
            }

            Sizer(50)
            Text(
                "What is Your Location?",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (24 / fontScall).sp,
                color = CustomColor.primaryColor950,
                textAlign = TextAlign.Center

            )
            Sizer(8)
            Text(
                "We need to know your loacation in order to suggest nearby services.",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor800,
                textAlign = TextAlign.Center
            )
            Sizer(50)
            CustomBotton(
                //isLoading = isLoading.value,
                operation = {
                    requestPermssion.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                },
                buttonTitle = "Allow Location Access",
                color = CustomColor.primaryColor700
            );
            Sizer(20)
            CustomTitleBotton(
                operation = {
                    nav.navigate(Screens.LocationList(true))
                },
                buttonTitle = "Enter Location Manually",
                color = CustomColor.primaryColor700
            );




            if(isNotEnablePermission.value)
            {
                AlertDialog(
                    onDismissRequest = {
                        //Logic when dismiss happens
                        isNotEnablePermission.value=false
                    },
                    title = {
                        Text("Permission Required")
                    },
                    text = {
                        Text("You need to approve this permission in order to...")
                    },
                    confirmButton = {
//                        TextButton(onClick = {
//                            Logic when user confirms to accept permissions
//                        }) {
//                            Text("Confirm")
//                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            //Logic when user denies to accept permissions
                        }) {
                            isNotEnablePermission.value=false;
                            Text("Deny")
                        }
                    })
            }
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