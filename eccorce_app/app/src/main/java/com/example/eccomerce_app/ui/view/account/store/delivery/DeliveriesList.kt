package com.example.eccomerce_app.ui.view.account.store.delivery

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.e_commercompose.model.Category
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.e_commercompose.model.enMapType
import com.example.e_commercompose.ui.component.BannerBage
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.ProductLoading
import com.example.e_commercompose.ui.component.ProductShape
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.component.TextNumberInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.ui.view.account.store.enOperation
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.eccomerce_app.util.General.toCustomFil
import com.example.eccomerce_app.util.General.toLocalDateTime
import com.example.eccomerce_app.viewModel.DeliveryViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import java.util.UUID
import kotlin.text.ifEmpty


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesListScreen(
    nav: NavHostController,
    deliveryViewModel: DeliveryViewModel
) {

    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    val lazyState = rememberLazyListState()


    val snackBarHostState = remember { SnackbarHostState() }

    val errorMessage = remember { mutableStateOf("") }

    val page = remember { mutableIntStateOf(1) }


    val isRefresh = remember { mutableStateOf(false) }
    val isSendingData = remember { mutableStateOf(false) }
    val isAddingDialog = remember { mutableStateOf(false) }

    val userId = remember { mutableStateOf(TextFieldValue("")) }

    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }


    fun clearTextInpu(){
        userId.value= TextFieldValue("")
        errorMessage.value=""
    }
    fun createDeliveryMan() {
        coroutine.launch {
            isSendingData.value = true
            isAddingDialog.value = false
            val result = deliveryViewModel.createDelivery(UUID.fromString(userId.value.text))
            isSendingData.value = false
            if (result.isNullOrEmpty()) {
                clearTextInpu()
                snackBarHostState.showSnackbar("Delivery Created Successfully")
                return@launch
            }
            snackBarHostState.showSnackbar(result)

        }
    }




    LaunchedEffect(reachedBottom.value) {
        /* if (!products.value.isNullOrEmpty() && reachedBottom.value && products.value!!.size > 23) {
             when (selectedSubCategoryId.value == null) {
                 true -> {
                     productViewModel.getProducts(
                         page, storeId = storeId!!, isLoadingMore
                     )
                 }

                 else -> {
                     productViewModel.getProducts(
                         page, storeId = storeId!!, selectedSubCategoryId.value!!, isLoadingMore
                     )
                 }
             }

         }
         */

    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },

        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(end = 15.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        "Deliveries",
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
                        }) {
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
        },
        floatingActionButton = {

            FloatingActionButton(
                onClick = { isAddingDialog.value = true },
                containerColor = CustomColor.primaryColor500
            ) {
                Icon(
                    Icons.Default.Add,
                    "", tint = Color.White
                )
            }

        }

    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()


        if (isSendingData.value)
            Dialog(
                onDismissRequest = {})
            {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(90.dp)
                        .background(
                            Color.White, RoundedCornerShape(15.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CustomColor.primaryColor700, modifier = Modifier.size(40.dp)
                    )
                }
            }


        if (isAddingDialog.value)
            Dialog(
                onDismissRequest = {
                    isAddingDialog.value = false
                    clearTextInpu()
                }
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(7.dp))
                        .padding(horizontal = 10.dp)

                ) {
                    Sizer(10)
                    TextInputWithTitle(
                        userId,
                        title = "", "User Id",
                        errorMessage = errorMessage.value,
                        isHasError = errorMessage.value.isNotEmpty(),
                        maxLines = 2
                    )

                    if (errorMessage.value.isNotEmpty())
                        Sizer(heigh = 10)

                    CustomAuthBottom(
                        isLoading = isSendingData.value,
                        operation = {
                            createDeliveryMan()
                        },
                        validationFun = {
                            if (userId.value.text.isEmpty()) {
                                errorMessage.value = "User Id must not be empty"
                                return@CustomAuthBottom false
                            } else return@CustomAuthBottom true
                        },
                        buttonTitle = "Create",
                        isHasBottomPadding = false
                    )
                    Sizer(10)
                }
            }

        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                //    getStoreInfoByStoreId(storeId!!)
            },
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()

        )
        {


        }
    }


}