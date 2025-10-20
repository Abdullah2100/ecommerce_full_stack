package com.example.eccomerce_app.ui.view.address

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.viewModel.UserViewModel
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.util.General
import com.example.e_commerc_delivery_man.model.enMapType
import com.example.e_commerc_delivery_man.ui.component.CustomBotton
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressHomeScreen(
    nav: NavHostController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val fontScall = LocalDensity.current.fontScale

    val coroutine = rememberCoroutineScope()

    val isNotEnablePermission = remember { mutableStateOf(false) }


    val snackBarHostState = remember { SnackbarHostState() }


    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val arePermissionsGranted = permissions.values.reduce { acc, next -> acc && next }

            if (arePermissionsGranted) {


                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return@rememberLauncherForActivityResult
                } else {
                    locationClient.lastLocation
                        .apply {
                            addOnSuccessListener { location ->

                                location?.toString()
                                if (location != null)
                                    nav.navigate(
                                        Screens.Map(
                                            lognit = location.longitude,
                                            latitt = location.latitude,
                                            isFromLogin = true,
                                            mapType = enMapType.My
                                        )
                                    )
                                else
                                    coroutine.launch {
                                        snackBarHostState.showSnackbar("you should enable location services")
                                    }
                            }
                            addOnFailureListener { fail ->
                                Log.d(
                                    "contextError",
                                    "the current location is null ${fail.stackTrace}"
                                )

                            }
                        }


                }

            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )


    LaunchedEffect(Unit) {
        userViewModel.getMyInfo()
    }

    Scaffold(


        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
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
                "We need to know your location in order to suggest nearby services.",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor800,
                textAlign = TextAlign.Center
            )
            Sizer(50)
            CustomBotton(
                operation = {
                    requestPermission.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                buttonTitle = "Pick Current Place From Map",
                color = CustomColor.primaryColor700
            )
            Sizer(20)




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