package com.example.e_commercompose.ui.view.Address

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.model.Address
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.viewModel.HomeViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    nav: NavHostController,
    homeViewModle: HomeViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val locationss = homeViewModle.myInfo.collectAsState()
    val addressHolder = remember { mutableStateOf<Address?>(null) }

    val coroutine = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf<Boolean>(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val isPressAddNewAddress = remember { mutableStateOf(false) }
    val addressTitle = remember { mutableStateOf(TextFieldValue("")) }

    val sheetState = rememberModalBottomSheetState()
    val isRefresh = remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                            Icons.Default.KeyboardArrowLeft,
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
            when (!isPressAddNewAddress.value) {
                true -> {
                    BottomAppBar(
                        containerColor = Color.White
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        ) {
                            CustomBotton(
                                operation = {
                                    isPressAddNewAddress.value = true
                                },
                                buttonTitle = "Add New"
                            )
                        }
                    }
                }

                else -> {
                    ModalBottomSheet(
                        onDismissRequest = {
                            isPressAddNewAddress.value = false
                        },
                        sheetState = sheetState,

                    ) {

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                        ) {

                            TextInputWithTitle(
                                value = addressTitle,
                                placHolder = addressHolder?.value?.title ?: "Insert Address Name",
                                title = "Address Title"
                            )
                            CustomBotton(
                                operation = {
                                    keyboardController?.hide()
                                    when(addressHolder.value==null){
                                        true->{}
                                        else->{
                                            coroutine.launch {

                                                isLoading.value=true
                                                var result = async {
                                                    homeViewModle.updateUserAddress(
                                                        addressHolder.value!!.id!!,
                                                        addressTitle = addressTitle.value.text,
                                                        null,
                                                        null
                                                    )
                                                }.await()

                                                isLoading.value=false
                                                var message = "Update Adddress Data Seccessfuly"

                                               if(result!=null){
                                                  message= result
                                               }
                                                else{
                                                   isPressAddNewAddress.value=false;

                                               }
                                                snackbarHostState.showSnackbar(message)

                                            }
                                        }
                                    }
                                },
                                buttonTitle =if(addressHolder.value!=null) "Update" else  "Add New"
                            )
                            if(addressHolder.value!=null&&addressHolder.value!!.isCurrnt==false)
                            {
                                Sizer(10)
                                CustomBotton(
                                    operation = {
                                        keyboardController?.hide()
                                        when(addressHolder.value==null){
                                            true->{}
                                            else->{
                                                coroutine.launch {

                                                    isLoading.value=true
                                                    var result = async {
                                                        homeViewModle.deleteUserAddress(
                                                            addressHolder.value!!.id!!)
                                                    }.await()

                                                    isLoading.value=false
                                                    var message = "delete Adddress Data Seccessfuly"

                                                    if(result!=null){
                                                        message= result
                                                    }
                                                    else{
                                                        isPressAddNewAddress.value=false;

                                                    }
                                                    snackbarHostState.showSnackbar(message)

                                                }
                                            }
                                        }

                                    },
                                    buttonTitle ="Delete",
                                    color = CustomColor.alertColor_1_600
                                )
                            }
                        }

                    }
                }
            }
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()
        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {homeViewModle.getMyInfo()}
        ) {
            when (locationss.value?.address.isNullOrEmpty()) {
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

                        items(locationss.value!!.address!!.size)
                        { index ->

                            Row(
                                modifier = Modifier
                                    .padding(bottom = 5.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .combinedClickable(
                                        onClick = {
                                            if (!locationss.value!!.address!![index].isCurrnt) {
                                                coroutine.launch {
                                                    isLoading.value = true
                                                    var result = async {
                                                        homeViewModle.setCurrentActiveUserAddress(
                                                            locationss.value!!.address!![index].id!!,
                                                        )
                                                    }.await()
                                                    isLoading.value = false
                                                    var message =
                                                        "update Current Address Seccessfuly"
                                                    if (result != null) {
                                                        message = result
                                                    }
                                                    snackbarHostState.showSnackbar(message)
                                                }

                                            }


                                        },
                                        onLongClick = {
                                            addressHolder.value = locationss.value?.address!![index]
                                            isPressAddNewAddress.value = true
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
                                    Row() {
                                        Text(
                                            locationss.value!!.address!![index].title.toString(),
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center

                                        )
                                        if (locationss.value!!.address!![index].isCurrnt) {
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
                                    selected = locationss.value!!.address!![index].isCurrnt,
                                    onClick = {
                                        if (!locationss.value!!.address!![index].isCurrnt) {
                                            coroutine.launch {
                                                isLoading.value = true
                                                var result = async {
                                                    homeViewModle.setCurrentActiveUserAddress(
                                                        locationss.value!!.address!![index].id!!,
                                                    )
                                                }.await()
                                                isLoading.value = false
                                                var message = "update Current Address Seccessfuly"
                                                homeViewModle.getMyInfo()
                                                if (result != null) {
                                                    message = result
                                                }
                                                snackbarHostState.showSnackbar(message)
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