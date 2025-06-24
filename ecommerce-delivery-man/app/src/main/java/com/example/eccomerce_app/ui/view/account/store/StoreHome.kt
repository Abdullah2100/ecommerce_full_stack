package com.example.e_commerc_delivery_man.ui.view.account.store

import android.Manifest
import android.util.Log
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.LocationOn
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
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.Util.General.reachedBottom
import com.example.e_commerc_delivery_man.Util.General.toCustomFil
import com.example.e_commerc_delivery_man.Util.General.toLocalDateTime
import com.example.e_commerc_delivery_man.model.Category
import com.example.e_commerc_delivery_man.model.SubCategoryUpdate
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.BannerBage
import com.example.e_commerc_delivery_man.ui.component.CustomBotton
import com.example.e_commerc_delivery_man.ui.component.ProductLoading
import com.example.e_commerc_delivery_man.ui.component.ProductShape
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.component.TextInputWithTitle
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.UUID
import kotlin.collections.forEach


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel,
    store_idCopy: String?,
    isFromHome: Boolean?
) {
    val isShownSubCategoryBottomSheet = remember { mutableStateOf(false) }
    val isUpdated = remember { mutableStateOf(false) }
    val isDeleted = remember { mutableStateOf(false) }
    val selectedSubCategoryId = remember { mutableStateOf<UUID?>(null) }
    val subCategoryName = remember { mutableStateOf(TextFieldValue("")) }
    val isSubCategoryCreateError = remember { mutableStateOf(false) }
    val isChangeSubCategory = remember { mutableStateOf(false) }
    var isExpandedCategory = remember { mutableStateOf(false) }


    var store_id = if (store_idCopy == null) null else UUID.fromString(store_idCopy)

    val myInfo = homeViewModel.myInfo.collectAsState();
    var myStore_id = myInfo.value?.store_id

    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    val categories = homeViewModel.categories.collectAsState()

    val stores = homeViewModel.stores.collectAsState()
    val storeData = stores.value?.firstOrNull { it.id == store_id }

    val banners = homeViewModel.banners.collectAsState()
    val storeBanners = banners.value?.filter { it.store_id == store_id }

    val address = homeViewModel.storeAddress.collectAsState()
    val storeAddress = stores.value?.filter { it.id == store_id }

    val subcategories = homeViewModel.subCategories.collectAsState()
    val storeSubCategories = subcategories.value?.filter { it.store_id == store_id }

    val products = homeViewModel.products.collectAsState()
    val storeProduct =
        if (products.value != null && store_id != null) products.value!!.filter { it.store_id == store_id }
        else emptyList();

    val storeFilter = if (selectedSubCategoryId.value == null) storeProduct
    else storeProduct.filter { it.subcategory_id == selectedSubCategoryId.value }

    val isLoading = homeViewModel.isLoading.collectAsState()


    val wall_paper_image = remember { mutableStateOf<File?>(null) }
    val small_paper_image = remember { mutableStateOf<File?>(null) }
    val storeName = remember { mutableStateOf(TextFieldValue("")) }
    val longint = remember { mutableStateOf(0.0) }
    val latit = remember { mutableStateOf(0.0) }

    val isPigImage = remember { mutableStateOf<Boolean?>(null) }


    val errorMessage = remember { mutableStateOf("") }


    val isPressAddNewAddress = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val currutine = rememberCoroutineScope()
    val isNotEnablePermission = remember { mutableStateOf(false) }

    val categoryName = remember { mutableStateOf(TextFieldValue("")) }


    val snackbarHostState = remember { SnackbarHostState() }


    val bannerImage = remember { mutableStateOf<File?>(null) }

    val isShownDateDialog = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val currentTime = Calendar.getInstance().timeInMillis;
    val isSendingData = remember { mutableStateOf(false) }


    var animated = animateDpAsState(
        if (isExpandedCategory.value) ((categories.value?.size ?: 1) * 35).dp else 0.dp
    )

    var rotation = animateFloatAsState(
        if (isExpandedCategory.value) 180f else 0f
    )
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val requestPermssion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { permission ->
            val arePermissionsGranted = permission.values.reduce { acc, next ->
                acc && next
            }
            if (arePermissionsGranted) {
                currutine.launch(Dispatchers.Main) {

                    try {
                        val data = fusedLocationClient.lastLocation.await()
                        data?.let { location ->
                            longint.value = location.longitude ?: 5.5000
                            latit.value = location.latitude ?: 5.5000
                        }
                        if (longint.value == 0.0) {
                            longint.value = 5.50000
                            latit.value = 5.50000
                        }

                    } catch (e: SecurityException) {
                        var error = "Permission exception: ${e.message}"
                    }

                }
            } else {
                isNotEnablePermission.value = true
            }
        })

    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context);
            if (fileHolder != null) {
                when (isPigImage.value == null) {
                    true -> {
                        bannerImage.value = fileHolder
                        isShownDateDialog.value = true
                    }

                    else -> {
                        when (isPigImage.value) {
                            true -> {
                                wall_paper_image.value = fileHolder
                            }

                            else -> {
                                small_paper_image.value = fileHolder
                            }
                        }
                    }
                }
                isPigImage.value = null
            }
        }
    }

    val lazyState = rememberLazyListState()
    val reachedBottom = remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }
    var page = remember { mutableStateOf(1) }
    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }



    LaunchedEffect(reachedBottom.value) {
        if (!products.value.isNullOrEmpty() && reachedBottom.value) {
            when (selectedSubCategoryId.value == null) {
                true -> {
                    homeViewModel.getProducts(
                        page, store_id = store_id!!, isLoadingMore
                    )
                }

                else -> {
                    homeViewModel.getProducts(
                        page, store_id = store_id!!, selectedSubCategoryId.value!!, isLoadingMore
                    )
                }
            }

        }

    }



    fun creationValidation(): Boolean {
        keyboardController?.hide()
        var errorMessage = "";
        if (wall_paper_image.value == null) errorMessage = "You must select the wallpaper image"
        else if (small_paper_image.value == null) errorMessage = "You must select the small image"
        else if (longint.value == 0.0) errorMessage = "You must select the store Location"
        else if (storeName.value.text.isEmpty()) errorMessage = "You write the store name"
        if (errorMessage.trim().isNotEmpty()) {
            currutine.launch {
                snackbarHostState.showSnackbar(errorMessage)
            }
            return false
        }
        return true

    }


    LaunchedEffect(Unit) {
        if (store_id != null) homeViewModel.getStoreInfoByStoreId(store_id)
    }




    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState, modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },

        bottomBar = {
            if (isPressAddNewAddress.value) ModalBottomSheet(
                onDismissRequest = {
                    isPressAddNewAddress.value = false
                }, sheetState = sheetState
            ) {

                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isPressAddNewAddress.value = false
                                requestPermssion.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }, horizontalArrangement = Arrangement.Start
                    ) {
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
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

            if (isShownSubCategoryBottomSheet.value)
                ModalBottomSheet(
                    onDismissRequest = {

                        isShownSubCategoryBottomSheet.value = false
                        isExpandedCategory.value = false
                        categoryName.value = TextFieldValue("")
                        subCategoryName.value = TextFieldValue("")

                    },
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Category",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.End

                            )
                            Sizer(10)
                            Row(
                                modifier = Modifier
                                    .height(65.dp)
                                    .fillMaxWidth()
                                    .border(
                                        1.dp, CustomColor.neutralColor400, RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        isExpandedCategory.value = !isExpandedCategory.value
                                    }
                                    .padding(horizontal = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {

                                Text(
                                    if (categoryName.value.text.isEmpty()) "Select Category Name"
                                    else categoryName.value.text
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    "",
                                    modifier = Modifier.rotate(rotation.value)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(bottom = 19.dp)
                                    .fillMaxWidth()
                                    .height(animated.value)
                                    .border(
                                        1.dp, CustomColor.neutralColor200, RoundedCornerShape(
                                            topStart = 4.dp,
                                            topEnd = 4.dp,
                                            bottomStart = 8.dp,
                                            bottomEnd = 8.dp
                                        )
                                    ),

                                ) {
                                categories.value?.forEach { option: Category ->
                                    Text(
                                        option.name,
                                        modifier = Modifier
                                            .height(50.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                isExpandedCategory.value = false
                                                categoryName.value = TextFieldValue(option.name)
                                            }
                                            .padding(top = 12.dp, start = 5.dp)

                                    )
                                }
                            }
                        }


                        TextInputWithTitle(
                            title = "Name",
                            value = subCategoryName,
                            placHolder = "Enter Sub Category Name",
                        )

                        CustomBotton(
                            isLoading = !isDeleted.value && isSendingData.value,
                            operation = {
                                currutine.launch {
                                    keyboardController?.hide()
                                    var result = async {
                                        isSendingData.value = true;
                                        if (isUpdated.value) homeViewModel.updateSubCategory(
                                            SubCategoryUpdate(
                                                name = subCategoryName.value.text,
                                                cateogy_id = categories.value!!.firstOrNull() { it.name == categoryName.value.text }!!.id,
                                                id = selectedSubCategoryId.value!!

                                            )
                                        )
                                        else homeViewModel.createSubCategory(
                                            name = subCategoryName.value.text,
                                            categoryId = categories.value!!.firstOrNull() { it.name == categoryName.value.text }!!.id
                                        )
                                    }.await()
                                    isSendingData.value = false
                                    if (result.isNullOrEmpty()) {
                                        isShownSubCategoryBottomSheet.value = false
                                        isExpandedCategory.value = false
                                        categoryName.value = TextFieldValue("")
                                        subCategoryName.value = TextFieldValue("")
                                        if (isUpdated.value) {
                                            isUpdated.value = false
                                            selectedSubCategoryId.value = UUID.randomUUID()
                                        }
                                    } else {
                                        isSubCategoryCreateError.value = true
                                        errorMessage.value = result
                                    }

                                }
                            },
                            buttonTitle = if (isUpdated.value) "Update" else "Create",
                            color = null,
                            isEnable = isDeleted.value == false &&
                                    (
                                            subCategoryName.value.text.isNotEmpty() &&
                                                    categoryName.value.text.isNotEmpty()
                                            )
                        )

                        if (isUpdated.value) {
                            Sizer(10)
                            CustomBotton(
                                isLoading = isDeleted.value && isSendingData.value,
                                operation = {
                                    currutine.launch {
                                        isSendingData.value = true
                                        isDeleted.value = true
                                        keyboardController?.hide()
                                        var result = async {
                                            homeViewModel.deleteSubCategory(
                                                id = selectedSubCategoryId.value!!
                                            )
                                        }.await()
                                        isSendingData.value = false
                                        isDeleted.value = false
                                        if (result.isNullOrEmpty()) {
                                            isShownSubCategoryBottomSheet.value = false
                                            isExpandedCategory.value = false
                                            categoryName.value = TextFieldValue("")
                                            subCategoryName.value = TextFieldValue("")
                                            if (isUpdated.value) {
                                                isUpdated.value = false
                                                selectedSubCategoryId.value = UUID.randomUUID()
                                            }
                                        } else {
                                            isSubCategoryCreateError.value = true
                                            errorMessage.value = result
                                        }

                                    }
                                },
                                buttonTitle = "Deleted",
                                color = CustomColor.alertColor_1_600,
                                isEnable = isSendingData.value == false
                            )
                        }
                        if (isSubCategoryCreateError.value) {
                            AlertDialog(
                                containerColor = Color.White, onDismissRequest = {
                                    isSubCategoryCreateError.value = false
                                },

                                text = {

                                    Text(
                                        errorMessage.value,
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.End

                                    )
                                }, confirmButton = {

                                }, dismissButton = {
                                    TextButton(onClick = {
                                        isSubCategoryCreateError.value = false
                                    }) {

                                        Text(
                                            "cencle",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = (16).sp,
                                            color = CustomColor.neutralColor700,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                })
                        }

                    }
                }


        }, modifier = Modifier
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
                        "Store",
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
                            Icons.Default.KeyboardArrowLeft,
                            "",
                            modifier = Modifier.size(30.dp),
                            tint = CustomColor.neutralColor950
                        )
                    }
                },
                actions = {
                    if (myStore_id == null && isFromHome == false) {
                        TextButton(
                            enabled = isLoading.value == false, onClick = {
                                if (creationValidation()) {
                                    keyboardController?.hide()
                                    currutine.launch {
                                        var result = async {

                                            homeViewModel.createStore(
                                                name = storeName.value.text,
                                                wallpaper_image = wall_paper_image.value!!,
                                                small_image = small_paper_image.value!!,
                                                longitude = longint.value,
                                                latitude = latit.value,
                                            );
                                        }.await()

                                        if (result != null) {
                                            snackbarHostState.showSnackbar(result)
                                        } else {
                                            wall_paper_image.value = null
                                            small_paper_image.value = null
                                            storeName.value = TextFieldValue("")
                                            longint.value = 0.0;
                                            latit.value = 0.0
                                        }
                                    }
                                }
                            }) {
                            when (isLoading.value) {
                                true -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp), strokeWidth = 2.dp
                                    )
                                }

                                else -> {
                                    Text(
                                        "Create",
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.primaryColor700,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else if (isFromHome == false && myStore_id != null && myStore_id == store_id && isFromHome == false && (storeName.value.text.isNotEmpty() || latit.value != 0.0 || longint.value != 0.0 || wall_paper_image.value != null || small_paper_image.value != null)) {
                        TextButton(
                            enabled = isLoading.value == false, onClick = {
                                keyboardController?.hide()
                                currutine.launch {
                                    var result = async {

                                        homeViewModel.updateStore(
                                            name = storeName.value.text,
                                            wallpaper_image = wall_paper_image.value,
                                            small_image = small_paper_image.value,
                                            longitude = longint.value,
                                            latitude = latit.value,
                                        );
                                    }.await()

                                    if (result != null) {
                                        snackbarHostState.showSnackbar(result)
                                    } else {
                                        wall_paper_image.value = null
                                        small_paper_image.value = null
                                        storeName.value = TextFieldValue("")
                                    }
                                }
                            }) {
                            when (isLoading.value) {
                                true -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp), strokeWidth = 2.dp
                                    )
                                }

                                else -> {
                                    Text(
                                        "Update",
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.primaryColor700,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (isFromHome == false && storeData != null) FloatingActionButton(
                onClick = {
                    nav.navigate(Screens.CreateProduct(store_id.toString(), null))
                }, containerColor = CustomColor.primaryColor50
            ) {
                Icon(
                    Icons.Default.Add, "", tint = Color.Black
                )
            }
        }

    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()


        if (isSendingData.value) Dialog(
            onDismissRequest = {}) {
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

        if (isShownDateDialog.value) DatePickerDialog(
            modifier = Modifier.background(Color.White),
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (datePickerState?.selectedDateMillis != null && datePickerState.selectedDateMillis!! < currentTime) {
                            isShownDateDialog.value = false
                            currutine.launch {
                                snackbarHostState.showSnackbar("You must select valide date")
                            }
                        } else {
                            isShownDateDialog.value = false
                            isSendingData.value = true
                            currutine.launch {
                                val dattime = Calendar.getInstance().apply {
                                    timeInMillis = datePickerState.selectedDateMillis!!
                                }
                                var result = async {
                                    homeViewModel.createBanner(
                                        end_date = dattime.toLocalDateTime().toString().toString(),
                                        image = bannerImage.value!!
                                    )
                                }.await()
                                isSendingData.value = false
                                var errorMessage = result
                                if (result.isNullOrEmpty()) errorMessage =
                                    "banner created seccesffuly";
                                else errorMessage = result;
                                currutine.launch {
                                    snackbarHostState.showSnackbar(
                                        errorMessage
                                    )

                                }
                            }

                        }
                    }) {
                    Text(
                        "ok",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (18).sp,
                        color = CustomColor.primaryColor700,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            dismissButton = { /*TODO*/ }) {
            DatePicker(state = datePickerState)
        }



        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {homeViewModel.getStoreInfoByStoreId(store_id!!)}

        ){
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(top = it.calculateTopPadding() - 29.dp)
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {

                item {
                    ConstraintLayout(
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                    ) {

                        val (bigImageRef, smalImageRef) = createRefs()

                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .constrainAs(bigImageRef) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }) {
                            val (imageRef, cameralRef) = createRefs()
                            Box(
                                modifier = Modifier
                                    .constrainAs(imageRef) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    }
                                    .height(150.dp)
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = if (isFromHome == true) Color.Transparent else CustomColor.neutralColor500,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        color = if (isFromHome == true) CustomColor.primaryColor50
                                        else Color.White,
                                    ), contentAlignment = Alignment.Center) {
                                when (wall_paper_image.value == null) {
                                    true -> {
                                        when (storeData?.pig_image.isNullOrEmpty()) {
                                            true -> {
                                                if (isFromHome == false) Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                                                    "",
                                                    modifier = Modifier.size(80.dp),
                                                    tint = CustomColor.neutralColor200
                                                )
                                            }

                                            else -> {

                                                SubcomposeAsyncImage(
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    model = General.handlingImageForCoil(
                                                        storeData.pig_image.toString(), context
                                                    ),
                                                    contentDescription = "",
                                                    loading = {
                                                        Box(
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                                        ) {
                                                            CircularProgressIndicator(
                                                                color = Color.Black,
                                                                modifier = Modifier.size(54.dp) // Adjust the size here
                                                            )
                                                        }
                                                    },
                                                )
                                            }
                                        }
                                    }

                                    else -> {
                                        SubcomposeAsyncImage(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
//                                                .padding(top = 35.dp)
                                                .fillMaxHeight()
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp)),
                                            model = General.handlingImageForCoil(
                                                wall_paper_image.value!!.absolutePath, context
                                            ),
                                            contentDescription = "",
                                            loading = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                                ) {
                                                    CircularProgressIndicator(
                                                        color = Color.Black,
                                                        modifier = Modifier.size(54.dp) // Adjust the size here
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }

                            }
                            if (isFromHome == false) Box(
                                modifier = Modifier
                                    .padding(end = 5.dp, bottom = 10.dp)
                                    .constrainAs(cameralRef) {
                                        end.linkTo(imageRef.end)
                                        bottom.linkTo(imageRef.bottom)
                                    }


                            ) {

                                IconButton(
                                    onClick = {
                                        keyboardController?.hide()
                                        isPigImage.value = true;
                                        onImageSelection.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier.size(30.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = CustomColor.primaryColor200
                                    )
                                ) {
                                    Icon(
                                        ImageVector.vectorResource(R.drawable.camera),
                                        "",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.White
                                    )
                                }
                            }

                        }

                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = -50.dp)
                                .constrainAs(smalImageRef) {
                                    top.linkTo(bigImageRef.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }) {
                            val (imageRef, cameralRef) = createRefs()
                            Box(
                                modifier = Modifier
                                    .constrainAs(imageRef) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    }
                                    .height(110.dp)
                                    .width(110.dp)
                                    .border(
                                        width = 1.dp,
                                        color = if (isFromHome == true) Color.Transparent else CustomColor.neutralColor500,
                                        shape = RoundedCornerShape(60.dp)
                                    )
                                    .clip(RoundedCornerShape(60.dp))
                                    .background(
                                        color = if (isFromHome == true && storeData == null) CustomColor.primaryColor50
                                        else Color.White, shape = RoundedCornerShape(60.dp)
                                    ), contentAlignment = Alignment.Center) {
                                when (small_paper_image.value == null) {
                                    true -> {
                                        when (storeData?.small_image.isNullOrEmpty()) {
                                            true -> {
                                                if (isFromHome == false) Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                                                    "",
                                                    modifier = Modifier.size(50.dp),
                                                    tint = CustomColor.neutralColor200

                                                )
                                            }

                                            else -> {

                                                SubcomposeAsyncImage(
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
//                                                .padding(top = 35.dp)
                                                        .height(90.dp)
                                                        .width(90.dp)
                                                        .clip(RoundedCornerShape(50.dp)),
                                                    model = General.handlingImageForCoil(
                                                        storeData.small_image, context
                                                    ),
                                                    contentDescription = "",
                                                    loading = {
                                                        Box(
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                                        ) {
                                                            CircularProgressIndicator(
                                                                color = Color.Black,
                                                                modifier = Modifier.size(54.dp) // Adjust the size here
                                                            )
                                                        }
                                                    },
                                                )
                                            }
                                        }
                                    }

                                    else -> {
                                        SubcomposeAsyncImage(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
//                                                .padding(top = 35.dp)
                                                .height(90.dp)
                                                .width(90.dp)
                                                .clip(RoundedCornerShape(50.dp)),
                                            model = General.handlingImageForCoil(
                                                small_paper_image.value?.absolutePath, context
                                            ),
                                            contentDescription = "",
                                            loading = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                                ) {
                                                    CircularProgressIndicator(
                                                        color = Color.Black,
                                                        modifier = Modifier.size(54.dp) // Adjust the size here
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }

                            }
                            if (isFromHome == false) Box(
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .constrainAs(cameralRef) {
                                        end.linkTo(imageRef.end)
                                        bottom.linkTo(imageRef.bottom)
                                    }


                            ) {

                                IconButton(
                                    onClick = {
                                        keyboardController?.hide()
                                        isPigImage.value = false;
                                        onImageSelection.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier.size(30.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = CustomColor.primaryColor200
                                    )
                                ) {
                                    Icon(
                                        ImageVector.vectorResource(R.drawable.camera),
                                        "",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.White
                                    )
                                }
                            }

                        }

                    }

                    Sizer(20)
                }

                item {
                    when (isFromHome) {
                        true -> {
                            Box(
                                Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                            ) {
                                when (storeData == null) {
                                    true -> {
                                        Box(
                                            modifier = Modifier
                                                .width(150.dp)
                                                .height(30.dp)
                                                .background(
                                                    CustomColor.primaryColor50, RoundedCornerShape(8.dp)
                                                )
                                        )
                                    }

                                    else -> {
                                        Text(
                                            storeData.name,
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (24).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center,
                                        )
                                    }

                                }
                            }
                        }

                        else -> {
                            Sizer(10)

                            Text(
                                "Store Name",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (18).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center,
                            )
                            TextInputWithTitle(
                                value = storeName,
                                title = "",
                                placHolder = storeData?.name ?: "Write Your Store Name",
                                isHasError = false,
                            )


                        }
                    }

                }
                item {
                    if (isFromHome == false) {
                        Sizer(10)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Store Bannel",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (18).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center,
                            )
                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(70.dp)
                                    .background(
                                        CustomColor.primaryColor200, RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onImageSelection.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    }, contentAlignment = Alignment.Center

                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                        }
                    }
                }

                item {

                    when (banners.value == null) {
                        true -> {
                            Sizer(10)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(
                                        CustomColor.primaryColor50, RoundedCornerShape(8.dp)
                                    )
                            )
                        }

                        else -> {
                            if (!storeBanners.isNullOrEmpty()) BannerBage(
                                storeBanners,
                                true,
                                deleteBanner = if (isFromHome == true) null else { it ->
                                    isSendingData.value = true;
                                    currutine.launch {
                                        var result = async {
                                            homeViewModel.deleteBanner(it)
                                        }.await()

                                        isSendingData.value = false
                                        var errorMessage = ""
                                        if (result.isNullOrEmpty()) {
                                            errorMessage = "banner deleted Seccesffuly";
                                        } else {
                                            errorMessage = result
                                        }
                                        snackbarHostState.showSnackbar(errorMessage)
                                    }
                                })

                        }
                    }

                    Sizer(10)
                }

                item {

                    when (isFromHome) {
                        true -> {
                            when (address.value == null) {
                                true -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .background(
                                                CustomColor.primaryColor50, RoundedCornerShape(8.dp)
                                            )
                                    )
                                }

                                else -> {
                                    if (!storeAddress.isNullOrEmpty()) Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(
                                            "Store Location",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (18).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center,
                                        )
                                        IconButton(
                                            onClick = {
                                                when (isFromHome == false) {
                                                    true -> {
                                                        keyboardController?.hide()
                                                        isPressAddNewAddress.value = true
                                                    }

                                                    else -> {

                                                    }
                                                }
                                            }) {
                                            Icon(
                                                ImageVector.vectorResource(R.drawable.location_address_list),
                                                "",
                                                modifier = Modifier.size(24.dp),
                                                tint = CustomColor.primaryColor700
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    "Store Location",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (18).sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.Center,
                                )
                                IconButton(
                                    onClick = {
                                        when (isFromHome == false) {
                                            true -> {
                                                keyboardController?.hide()
                                                isPressAddNewAddress.value = true
                                            }

                                            else -> {

                                            }
                                        }
                                    }) {
                                    Icon(
                                        ImageVector.vectorResource(R.drawable.location_address_list),
                                        "",
                                        modifier = Modifier.size(24.dp),
                                        tint = CustomColor.primaryColor700
                                    )
                                }
                            }
                        }
                    }
                }

                item {

                    if (isNotEnablePermission.value) {
                        AlertDialog(onDismissRequest = {
                            isNotEnablePermission.value = false
                        }, title = {
                            Text("Permission Required")
                        }, text = {
                            Text("You need to approve this permission in order to...")
                        }, confirmButton = {}, dismissButton = {
                            TextButton(onClick = {
                                //Logic when user denies to accept permissions
                            }) {
                                isNotEnablePermission.value = false;
                                Text("Deny")
                            }
                        })
                    }
                }


                item {

                    AnimatedVisibility(
                        visible = (store_id != null || storeData != null)
                    ) {


                        Column(
                            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start
                        ) {
                            Sizer(10)

                            if (!subcategories.value.isNullOrEmpty()) {
                                Text(
                                    "Sub Category ",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (18).sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.Center,
                                )
                                Sizer(5)
                            }
                            LazyRow {
                                when ((subcategories.value == null)) {
                                    true -> {
                                        items(20) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(end = 5.dp)
                                                    .height(40.dp)
                                                    .width(90.dp)
                                                    .background(
                                                        CustomColor.primaryColor50,
                                                        RoundedCornerShape(8.dp)
                                                    ), contentAlignment = Alignment.Center

                                            ) {}
                                        }
                                    }

                                    else -> {
                                        if (!storeSubCategories.isNullOrEmpty()) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(end = 4.dp)
                                                        .height(40.dp)
                                                        .width(70.dp)
                                                        .background(
                                                            if (selectedSubCategoryId.value == null) CustomColor.alertColor_3_300 else Color.White,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (selectedSubCategoryId.value == null) Color.White else CustomColor.neutralColor200,
                                                            RoundedCornerShape(8.dp)

                                                        )
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .combinedClickable(
                                                            onClick = {
                                                                if (selectedSubCategoryId.value != null)
                                                                    selectedSubCategoryId.value = null
                                                            },
                                                        )
                                                    //
                                                    , contentAlignment = Alignment.Center

                                                ) {
                                                    Text(
                                                        "All",
                                                        fontFamily = General.satoshiFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (18).sp,
                                                        color = if (selectedSubCategoryId.value == null) Color.White else CustomColor.neutralColor200,
                                                        textAlign = TextAlign.Center,
                                                    )
                                                }

                                            }
                                            items(storeSubCategories?.size?:0) { index ->
                                                Box(
                                                    modifier = Modifier
                                                        .padding(end = 4.dp)
                                                        .height(40.dp)
                                                        .width(70.dp)
                                                        .background(
                                                            if (selectedSubCategoryId.value == storeSubCategories?.get(index)?.id) CustomColor.alertColor_3_300 else Color.White,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (selectedSubCategoryId.value == storeSubCategories?.get(index)?.id) Color.White else CustomColor.neutralColor200,
                                                            RoundedCornerShape(8.dp)

                                                        )
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .combinedClickable(onClick = {
                                                            currutine.launch {
                                                                if (
                                                                    selectedSubCategoryId.value !=
                                                                    subcategories.value?.get(index)?.id
                                                                ) {
                                                                    isChangeSubCategory.value = true
//                                                                                                                         delay(500)
                                                                    selectedSubCategoryId.value =
                                                                        storeSubCategories?.get(index)?.id
                                                                    isChangeSubCategory.value = false
                                                                }

                                                            }

                                                        }, onLongClick = {
                                                            if (isFromHome == false) {
                                                                val name = categories.value?.firstOrNull{it.id==
                                                                        subcategories.value!!.get(index).category_id}?.name?:""

                                                                categoryName.value= TextFieldValue(name)
                                                                subCategoryName.value= TextFieldValue(subcategories.value!!.get(index).name)
                                                                isUpdated.value = true

                                                                isShownSubCategoryBottomSheet.value =
                                                                    true

                                                            }
                                                        }), contentAlignment = Alignment.Center

                                                ) {
                                                    Text(
                                                        storeSubCategories!![index].name ?: "",
                                                        fontFamily = General.satoshiFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (18).sp,
                                                        color = if (selectedSubCategoryId.value ==storeSubCategories.get(index).id) Color.White else CustomColor.neutralColor200,

                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                            }

                                        }

                                    }
                                }

                                if (isFromHome == false) item {

                                    if (!storeSubCategories.isNullOrEmpty()) Sizer(width = 10)
                                    Box(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .width(70.dp)
                                            .background(
                                                CustomColor.primaryColor200, RoundedCornerShape(8.dp)
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                isUpdated.value = false
                                                isShownSubCategoryBottomSheet.value = true

                                            }, contentAlignment = Alignment.Center

                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            "",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            Sizer(10)
                            when (isChangeSubCategory.value) {
                                true -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(90.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = CustomColor.primaryColor200
                                        )
                                    }
                                }

                                else -> {
                                    if (isLoadingMore.value) {

                                        Box(
                                            modifier = Modifier
                                                .padding(top = 15.dp)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = CustomColor.primaryColor700)
                                        }
                                        Sizer(40)
                                    } else
                                        when (products.value == null) {
                                            true -> {
                                                ProductLoading(50)
                                            }

                                            else -> {
                                                if (storeFilter.isNotEmpty()) {
                                                    ProductShape(
                                                        storeFilter,
                                                        nav = nav,
                                                        delFun = if (isFromHome == true) null else { it ->
                                                            currutine.launch {
                                                                isSendingData.value = true
                                                                var result =
                                                                    homeViewModel.deleteProduct(
                                                                        store_id!!, it
                                                                    )

                                                                isSendingData.value = false
                                                                var resultMessage = ""
                                                                if (result == null) {
                                                                    resultMessage =
                                                                        "Product is Deleted successfuly"
                                                                } else {
                                                                    resultMessage = result
                                                                }
                                                                snackbarHostState.showSnackbar(
                                                                    resultMessage
                                                                )
                                                            }
                                                        },
                                                        updFun = if (isFromHome == true) null else { it ->
                                                            nav.navigate(
                                                                Screens.CreateProduct(
                                                                    store_id.toString(), it.toString()
                                                                )
                                                            )
                                                        })
                                                }
                                            }
                                        }


                                }
                            }

                        }

                    }
                }
                if (!isLoadingMore.value)
                    item {
                        Sizer(140)
                    }

            }

        }
    }


}