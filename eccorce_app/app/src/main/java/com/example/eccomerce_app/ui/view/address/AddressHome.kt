package com.example.e_commercompose.ui.view.location

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.viewModel.HomeViewModel
import com.example.e_commercompose.R
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.CustomTitleBotton
import com.example.e_commercompose.ui.component.Sizer
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressHomeScreen(
    nav: NavHostController,
    homeViewModle: HomeViewModel,
) {
    val context = LocalContext.current
    val fontScall = LocalDensity.current.fontScale


    val snackbarHostState = remember { SnackbarHostState() }


    val isNotEnablePermission = remember { mutableStateOf(false) }

    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val coroutine = rememberCoroutineScope()

    val requestPermssion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val arePermissionsGranted = permissions.values.reduce { acc, next -> acc && next }

            if (arePermissionsGranted) {


                locationClient.lastLocation
                    .apply {
                        addOnSuccessListener { location ->

                            location?.toString()
                            if (location != null)
                                nav.navigate(
                                    Screens.MapScreen(
                                        lognit = location.longitude,
                                        latitt = location.latitude,
                                        isFromLogin = true
                                    )
                                )
                            else
                                coroutine.launch {
                                    snackbarHostState.showSnackbar("you should enable location services")
                                }
                        }
                        addOnFailureListener { fail ->
                            Log.d(
                                "contextError",
                                "the current location is null ${fail.stackTrace}"
                            )

                        }
                    }


                // Got last known location. In some srare situations this can be null.
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        homeViewModle.getMyInfo()
    }

    Scaffold(


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
                .background(Color.White)
                .padding(horizontal = 15.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

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
                    requestPermssion.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                buttonTitle = "Allow Location Access",
                color = CustomColor.primaryColor700
            )
            Sizer(20)
            CustomTitleBotton(
                operation = {
                    homeViewModle.getMyInfo()
                    nav.navigate(Screens.LocationList(true))
                },
                buttonTitle = "Enter Location Manually",
                color = CustomColor.primaryColor700
            )




            if (isNotEnablePermission.value) {
                AlertDialog(
                    onDismissRequest = {
                        //Logic when dismiss happens
                        isNotEnablePermission.value = false
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
                            isNotEnablePermission.value = false
                            Text("Deny")
                        }
                    })
            }
        }


    }

}