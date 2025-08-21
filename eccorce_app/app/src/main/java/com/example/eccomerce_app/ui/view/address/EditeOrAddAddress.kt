package com.example.eccomerce_app.ui.view.address

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.model.Address
import com.example.e_commercompose.model.enMapType
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.UserViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrAddLocationScreen(
    nav: NavHostController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val userInfo = userViewModel.userInfo.collectAsState()


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutine = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val isLoading = remember { mutableStateOf<Boolean>(false) }
    val isRefresh = remember { mutableStateOf(false) }

    val currentAddress = remember { mutableStateOf<Address?>(null) }


    val requestPermissionThenNavigate = rememberLauncherForActivityResult(
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
                                        Screens.MapScreen(
                                            lognit = location.longitude,
                                            latitt = location.latitude,
                                            isFromLogin = false,
                                            title = currentAddress.value?.title,
                                            id = currentAddress.value?.id?.toString(),
                                            mapType = enMapType.My,
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

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Address",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
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
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {

            BottomAppBar(
                containerColor = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    CustomBotton(
                        operation = {
                            requestPermissionThenNavigate.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                )
                            )
                        },
                        buttonTitle = "Add New"
                    )
                }
            }

        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()
        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = { userViewModel.getMyInfo() }
        ) {
            when (userInfo.value?.address.isNullOrEmpty()) {
                true -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

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
                            fontSize = 20.sp,
                            color = CustomColor.neutralColor500,
                            textAlign = TextAlign.Center

                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(top = 100.dp)
                            .padding(horizontal = 15.dp)
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .background(CustomColor.neutralColor200)
                                    .fillMaxWidth()
                                    .height(1.dp)
                            )
                        }
                        item {
                            Sizer(30)

                            Text(
                                "Saved Address",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )

                            Sizer(20)
                        }

                        items(userInfo.value!!.address!!.size)
                        { index ->

                            Row(
                                modifier = Modifier
                                    .padding(bottom = 5.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .combinedClickable(
                                        onClick = {
                                            if (!userInfo.value!!.address!![index].isCurrent) {
                                                coroutine.launch {
                                                    isLoading.value = true
                                                    val result = async {
                                                        userViewModel.setCurrentActiveUserAddress(
                                                            userInfo.value!!.address!![index].id!!,
                                                        )
                                                    }.await()
                                                    isLoading.value = false
                                                    val message = result
                                                        ?: "update Current Address Successfully"

                                                    snackBarHostState.showSnackbar(message)
                                                }

                                            }


                                        },
                                        onLongClick = {
                                            currentAddress.value = userInfo.value?.address!![index]
                                            requestPermissionThenNavigate.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                )
                                            )

                                        }
                                    )
                                    .border(
                                        1.dp,
                                        CustomColor.neutralColor200,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 10.dp)
                                    .fillMaxWidth(),

                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Icon(
                                        imageVector = ImageVector
                                            .vectorResource(R.drawable.location_address_list),
                                        contentDescription = "",
                                        tint = CustomColor.neutralColor600,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Sizer(width = 20)
                                    Row {
                                        Text(
                                            userInfo.value!!.address!![index].title.toString(),
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )
                                        if (userInfo.value!!.address!![index].isCurrent) {
                                            Sizer(width = 5)
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        CustomColor.neutralColor100,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .height(20.dp)
                                                    .width(50.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "Default",
                                                    fontFamily = General.satoshiFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 11.sp,
                                                    color = CustomColor.neutralColor950,
                                                    textAlign = TextAlign.Center

                                                )
                                            }

                                        }
                                    }
                                }

                                RadioButton(
                                    selected = userInfo.value!!.address!![index].isCurrent,
                                    onClick = {
                                        if (!userInfo.value!!.address!![index].isCurrent) {
                                            coroutine.launch {
                                                isLoading.value = true
                                                val result = async {
                                                    userViewModel.setCurrentActiveUserAddress(
                                                        userInfo.value!!.address!![index].id!!,
                                                    )
                                                }.await()
                                                isLoading.value = false
                                                var message = "update Current Address Seccessfuly"
                                                userViewModel.getMyInfo()
                                                if (result != null) {
                                                    message = result
                                                }
                                                snackBarHostState.showSnackbar(message)
                                            }

                                        }
                                    }
                                )
                            }

                        }
                    }

                    if (isLoading.value == true)
                        Dialog(
                            onDismissRequest = {}
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(90.dp)
                                    .width(90.dp)
                                    .background(
                                        Color.White,
                                        RoundedCornerShape(15.dp)
                                    ), contentAlignment = Alignment.Center
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
}