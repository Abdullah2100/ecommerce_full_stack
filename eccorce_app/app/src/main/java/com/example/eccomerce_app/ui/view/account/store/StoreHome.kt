package com.example.e_commercompose.ui.view.account.store

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
import androidx.compose.runtime.MutableIntState
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
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.eccomerce_app.util.General.toCustomFil
import com.example.eccomerce_app.util.General.toLocalDateTime
import com.example.e_commercompose.model.Category
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.e_commercompose.model.enMapType
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.BannerBage
import com.example.e_commercompose.ui.component.CustomButton
import com.example.e_commercompose.ui.component.ProductLoading
import com.example.e_commercompose.ui.component.ProductShape
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.StoreViewModel
import com.example.eccomerce_app.viewModel.SubCategoryViewModel
import com.example.eccomerce_app.viewModel.BannerViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import com.example.eccomerce_app.viewModel.DeliveryViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import java.util.UUID
import kotlin.collections.forEach

enum class enOperation { STORE }
enum class enStoreOpeation { Create, Update }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    nav: NavHostController,
    copyStoreId: String?,
    isFromHome: Boolean?,
    bannerViewModel: BannerViewModel,
    categoryViewModel: CategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    storeViewModel: StoreViewModel,
    deliveryViewModel: DeliveryViewModel,
    productViewModel: ProductViewModel,
    userViewModel: UserViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()
    val datePickerState = rememberDatePickerState()
    val lazyState = rememberLazyListState()


    val createdStoreInfoHolder = storeViewModel.storeCreateData.collectAsState()
    val myInfo = userViewModel.userInfo.collectAsState()
    val categories = categoryViewModel.categories.collectAsState()
    val banners = bannerViewModel.banners.collectAsState()
    val subcategories = subCategoryViewModel.subCategories.collectAsState()
    val products = productViewModel.products.collectAsState()


    val operationType = remember { mutableStateOf<enOperation?>(null) }
    val storeOperation = remember { mutableStateOf<enStoreOpeation?>(null) }

    val selectedSubCategoryId = remember { mutableStateOf<UUID?>(null) }
    val selectedSubCategoryIdHolder = remember { mutableStateOf<UUID?>(null) }

    val bannerImage = remember { mutableStateOf<File?>(null) }

    val snackBarHostState = remember { SnackbarHostState() }

    val errorMessage = remember { mutableStateOf("") }

    val page = remember { mutableIntStateOf(1) }


    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }
    val isShownSubCategoryBottomSheet = remember { mutableStateOf(false) }
    val isUpdated = remember { mutableStateOf(false) }
    val isDeleted = remember { mutableStateOf(false) }
    val isSubCategoryCreateError = remember { mutableStateOf(false) }
    val isChangeSubCategory = remember { mutableStateOf(false) }
    val isExpandedCategory = remember { mutableStateOf(false) }
    val isShownDateDialog = remember { mutableStateOf(false) }
    val isPigImage = remember { mutableStateOf<Boolean?>(null) }
    val isSendingData = remember { mutableStateOf(false) }

    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }


    val storeId = if (copyStoreId == null) null else UUID.fromString(copyStoreId)


    val myStoreId = myInfo.value?.storeId
    val stores = storeViewModel.stores.collectAsState()
    val storeData = stores.value?.firstOrNull { it.id == (storeId ?: myStoreId) }
    val storeBanners = banners.value?.filter { it.storeId == storeId }
    val storeSubCategories = subcategories.value?.filter { it.storeId == storeId }
    val storeProduct =
        if (products.value != null && storeId != null) products.value!!.filter { it.storeId == storeId }
        else emptyList()
    val productFilterBySubCategory = if (selectedSubCategoryId.value == null) storeProduct
    else storeProduct.filter { it.subcategoryId == selectedSubCategoryId.value }


    //animation
    val animated = animateDpAsState(
        if (isExpandedCategory.value) ((categories.value?.size ?: 1) * 35).dp else 0.dp
    )

    val rotation = animateFloatAsState(
        if (isExpandedCategory.value) 180f else 0f
    )

    //text filed
    val storeName = remember {
        mutableStateOf(
            TextFieldValue(
                createdStoreInfoHolder.value?.name ?: ""
            )
        )
    }
    val categoryName = remember { mutableStateOf(TextFieldValue("")) }
    val subCategoryName = remember { mutableStateOf(TextFieldValue("")) }


    val currentTime = Calendar.getInstance().timeInMillis


    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    fun handlingLocation(type: enMapType, location: Location): LatLng? {
        return when (type) {
            enMapType.MyStore -> {
                Log.d("NavigationToMapHome", "fromMyStore")
                LatLng(
                    location.latitude,
                    location.longitude
                )
            }

            enMapType.Store -> {
                Log.d("NavigationToMapHome", "fromStore")

                if (storeData == null) null
                else LatLng(storeData.latitude, storeData.longitude)

            }

            else ->
                return null;
        }
    }

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
                } else locationClient.lastLocation
                    .apply {
                        addOnSuccessListener { location ->

                            location?.toString()
                            if (location != null) {
                                val type =
                                    when ((myStoreId == storeId || myStoreId == null) && isFromHome == false) {
                                        true -> enMapType.MyStore
                                        else -> enMapType.Store
                                    }
                                val locationHolder = handlingLocation(type, location)

                                nav.navigate(
                                    Screens.MapScreen(
                                        lognit = locationHolder?.longitude,
                                        latitt = locationHolder?.latitude,
                                        additionLat = if (type == enMapType.Store) location.latitude else null,
                                        additionLong = if (type == enMapType.Store) location.longitude else null,
                                        isFromLogin = false,
                                        title = null,
                                        mapType = type,
                                    )
                                )
                            } else
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


                // Got last known location. In some srare situations this can be null.
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )


    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    )
    { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context)
            if (fileHolder != null) {
                when (isPigImage.value == null) {
                    true -> {
                        bannerImage.value = fileHolder
                        isShownDateDialog.value = true
                    }

                    else -> {
                        when (isPigImage.value) {
                            true -> {
                                storeViewModel.setStoreCreateData(
                                    wallpaperImage = fileHolder,
                                    updateStoreOperation = storeOperation
                                )
                            }

                            else -> {
                                storeViewModel.setStoreCreateData(
                                    smallImage = fileHolder,
                                    updateStoreOperation = storeOperation
                                )
                            }
                        }
                    }
                }
                isPigImage.value = null
            }
        }
    }


    fun creationValidation(): Boolean {
        keyboardController?.hide()
        var errorMessage = ""
        if (createdStoreInfoHolder.value?.wallpaperImage == null) errorMessage =
            "You must select the wallpaper image"
        else if (createdStoreInfoHolder.value?.smallImage == null) errorMessage =
            "You must select the small image"
        else if (createdStoreInfoHolder.value?.name.isNullOrEmpty()) errorMessage =
            "You must write the store name"
        else if (createdStoreInfoHolder.value?.latitude == null) errorMessage =
            "You must select the store Location"

        if (errorMessage.trim().isNotEmpty()) {
            coroutine.launch {
                snackBarHostState.showSnackbar(errorMessage)
            }
            return false
        }
        return true

    }

    fun getStoreInfoByStoreId(id: UUID? = UUID.randomUUID()) {
        storeViewModel.getStoreData(storeId = id!!)
        bannerViewModel.getStoreBanner(id)
        subCategoryViewModel.getStoreSubCategories(id, 1)
        productViewModel.getProducts(mutableStateOf(1), id)
    }

    fun changeStoreOperation(storeOperationStore: enStoreOpeation?) {
        storeOperation.value = storeOperationStore
    }

    LaunchedEffect(Unit) {
        if (storeId != null) {
            getStoreInfoByStoreId(storeId)
        }
    }

    LaunchedEffect(reachedBottom.value) {
        if (!products.value.isNullOrEmpty() && reachedBottom.value && products.value!!.size > 23) {
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

    }

    LaunchedEffect(Unit) {
        changeStoreOperation(
            if (isFromHome == false && myStoreId == null)
                enStoreOpeation.Create
            else null
        )
    }




    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },

        bottomBar = {


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
                        )
                        {
                            Text(
                                "Category",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.End

                            )
                            Sizer(10)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp, CustomColor.neutralColor400,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))
//
                            )
                            {
                                Row(
                                    modifier = Modifier
                                        .height(65.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            isExpandedCategory.value = !isExpandedCategory.value
                                        }
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(horizontal = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically)
                                {

                                    Text(
                                        categoryName.value.text.ifEmpty { "Select Category Name" }
                                    )
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        "",
                                        modifier = Modifier.rotate(rotation.value)
                                    )
                                }

                                Column(
                                    modifier = Modifier
//                                           .padding(bottom = 19.dp)
                                        .fillMaxWidth()
                                        .height(animated.value)
                                        .border(
                                            1.dp,
                                            CustomColor.neutralColor200,
                                            RoundedCornerShape(
                                                topStart = 0.dp,
                                                topEnd = 0.dp,
                                                bottomStart = 8.dp,
                                                bottomEnd = 8.dp
                                            )
                                        ),

                                    )
                                {

                                    categories.value?.forEach { option: Category ->
                                        Text(
                                            option.name,
                                            modifier = Modifier
                                                .height(50.dp)
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    isExpandedCategory.value = false
                                                    categoryName.value =
                                                        TextFieldValue(option.name)
                                                }
                                                .padding(top = 12.dp, start = 5.dp)

                                        )
                                    }
                                }
                            }
                            Sizer(10)

                        }


                        TextInputWithTitle(
                            value = subCategoryName,
                            title = "Name",
                            placeHolder = "Enter Sub Category Name",
                        )

                        CustomButton(
                            operation = {
                                coroutine.launch {
                                    keyboardController?.hide()
                                    isSendingData.value = true

                                    val result = async {
                                        if (isUpdated.value) subCategoryViewModel.updateSubCategory(
                                            SubCategoryUpdate(
                                                name = subCategoryName.value.text,
                                                cateogyId = categories.value!!.firstOrNull() { it.name == categoryName.value.text }!!.id,
                                                id = selectedSubCategoryId.value!!
                                            )
                                        )
                                        else subCategoryViewModel.createSubCategory(
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
                                        }
                                    } else {
                                        isSubCategoryCreateError.value = true
                                        errorMessage.value = result
                                    }

                                }
                            },
                            buttonTitle = if (isUpdated.value) "Update" else "Create",
                            color = null,
                            isEnable = !isDeleted.value && (subCategoryName.value.text.isNotEmpty() &&
                                    categoryName.value.text.isNotEmpty())
                        )

                        if (isUpdated.value) {
                            Sizer(10)
                            CustomButton(
                                isLoading = isDeleted.value && isSendingData.value,
                                operation = {
                                    coroutine.launch {
                                        isSendingData.value = true
                                        isDeleted.value = true
                                        keyboardController?.hide()
                                        val result = async {
                                            subCategoryViewModel.deleteSubCategory(
                                                id = selectedSubCategoryIdHolder.value!!
                                            )
                                        }.await()
                                        isSendingData.value = false
                                        isDeleted.value = false
                                        if (result.isNullOrEmpty()) {
                                            isShownSubCategoryBottomSheet.value = false
                                            selectedSubCategoryIdHolder.value = null
                                            isExpandedCategory.value = false
                                            categoryName.value = TextFieldValue("")
                                            subCategoryName.value = TextFieldValue("")
                                            isUpdated.value = false
                                            selectedSubCategoryId.value = null
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
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            "",
                            modifier = Modifier.size(30.dp),
                            tint = CustomColor.neutralColor950
                        )
                    }
                },
                actions = {
                    if (isFromHome == false && storeOperation.value != null) {
                        TextButton(
                            enabled = !isSendingData.value,
                            onClick = {

                                if (myStoreId == null && !creationValidation()) {
                                    return@TextButton
                                }

                                keyboardController?.hide()
                                isSendingData.value = true
                                operationType.value = enOperation.STORE
                                coroutine.launch {
                                    val result = async {
                                        if (storeOperation.value == enStoreOpeation.Update)
                                            storeViewModel.updateStore(
                                                name = storeName.value.text,
                                                wallpaperImage = createdStoreInfoHolder.value?.wallpaperImage,
                                                smallImage = createdStoreInfoHolder.value?.smallImage,
                                                longitude = createdStoreInfoHolder.value?.longitude,
                                                latitude = createdStoreInfoHolder.value?.latitude,
                                            )
                                        else
                                            storeViewModel.createStore(
                                                name = createdStoreInfoHolder.value?.name
                                                    ?: storeName.value.text,
                                                wallpaperImage = createdStoreInfoHolder.value!!.wallpaperImage!!,
                                                smallImage = createdStoreInfoHolder.value!!.smallImage!!,
                                                longitude = createdStoreInfoHolder.value!!.longitude!!,
                                                latitude = createdStoreInfoHolder.value!!.latitude!!,
                                                sumAdditionalFun = { id ->
                                                    userViewModel.updateMyStoreId(
                                                        id
                                                    )
                                                    getStoreInfoByStoreId(id)
                                                }
                                            )
                                    }.await()
                                    changeStoreOperation( null)

                                    isSendingData.value = false
                                    operationType.value = null

                                    if (result != null) {
                                        snackBarHostState.showSnackbar(result)
                                    } else {
                                        storeName.value = TextFieldValue("")

                                    }
                                }


                            }) {
                            when (isSendingData.value && operationType.value == enOperation.STORE) {
                                true -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp), strokeWidth = 2.dp
                                    )
                                }

                                else -> {
                                    Text(
                                        if (storeOperation.value == enStoreOpeation.Update) "Update" else "Create",
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
            if (isFromHome == false && storeData != null)

                Column {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(bottom = 3.dp)
                            .size(50.dp),
                        onClick = {
                            deliveryViewModel.getDeliveryBelongToStore(mutableIntStateOf(1))
                            nav.navigate(Screens.DeliveriesList)
                        },
                        containerColor = CustomColor.primaryColor500
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.delivery_icon),
                            "", tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            nav.navigate(Screens.CreateProduct(storeId.toString(), null))
                        },
                        containerColor = CustomColor.primaryColor500
                    ) {
                        Icon(
                            Icons.Default.Add,
                            "", tint = Color.White
                        )
                    }

                }
        }

    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()


        if (isSendingData.value && operationType.value == null)
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

        if (isShownDateDialog.value)
            DatePickerDialog(
                modifier = Modifier
                    .padding(horizontal = 40.dp),

                onDismissRequest = {
                    isShownDateDialog.value = false
                },
                confirmButton =
                    {
                        TextButton(
                            onClick = {

                                if (datePickerState.selectedDateMillis != null && datePickerState.selectedDateMillis!! < currentTime) {
                                    isShownDateDialog.value = false
                                    coroutine.launch {
                                        snackBarHostState.showSnackbar("You must select valid date")
                                    }
                                } else {
                                    isShownDateDialog.value = false
                                    isSendingData.value = true
                                    coroutine.launch {
                                        val daytime = Calendar.getInstance().apply {
                                            timeInMillis = datePickerState.selectedDateMillis!!
                                        }
                                        val result = async {
                                            bannerViewModel.createBanner(
                                                endDate = daytime.toLocalDateTime().toString(),
                                                image = bannerImage.value!!,
                                            )
                                        }.await()
                                        isSendingData.value = false
                                        var errorMessage = ""
                                        errorMessage =
                                            if (result.isNullOrEmpty()) "banner created successfully"
                                            else result
                                        coroutine.launch {
                                            snackBarHostState.showSnackbar(
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
            )
            {
                DatePicker(state = datePickerState)
            }


        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = { getStoreInfoByStoreId(storeId!!) }

        )
        {

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
                                })
                        {
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isFromHome == true) CustomColor.neutralColor100 else CustomColor.neutralColor500,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        color = if (isFromHome == true) CustomColor.primaryColor50
                                        else Color.White,
                                    ), contentAlignment = Alignment.Center) {
                                when (createdStoreInfoHolder.value?.wallpaperImage == null) {
                                    true -> {
                                        when (storeData?.pigImage.isNullOrEmpty()) {
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
                                                        storeData.pigImage.toString(), context
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
                                                createdStoreInfoHolder.value!!.wallpaperImage!!.absolutePath,
                                                context
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
                                        isPigImage.value = true
                                        onImageSelection.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier.size(30.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = CustomColor.primaryColor500
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
                                })
                        {
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
                                    .clip(RoundedCornerShape(60.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isFromHome == true) CustomColor.neutralColor100 else CustomColor.neutralColor500,
                                        shape = RoundedCornerShape(60.dp)
                                    )
                                    .clip(RoundedCornerShape(60.dp))
                                    .background(
                                        color = if (isFromHome == true && storeData == null) CustomColor.primaryColor50
                                        else Color.White, shape = RoundedCornerShape(60.dp)
                                    ), contentAlignment = Alignment.Center) {
                                when (createdStoreInfoHolder.value?.smallImage == null) {
                                    true -> {
                                        when (storeData?.smallImage.isNullOrEmpty()) {
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
                                                        storeData.smallImage, context
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
                                                createdStoreInfoHolder.value!!.smallImage?.absolutePath,
                                                context
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
                                        isPigImage.value = false
                                        onImageSelection.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier.size(30.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = CustomColor.primaryColor500
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
                                                    CustomColor.primaryColor50,
                                                    RoundedCornerShape(8.dp)
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
                                placeHolder = storeData?.name ?: "Write Your Store Name",
                                isHasError = false,
                                onChange = { it ->
                                    storeViewModel
                                        .setStoreCreateData(
                                            storeTitle = it,
                                            updateStoreOperation = storeOperation
                                        )
                                },
                            )


                        }
                    }

                }

                if (isFromHome == true || myStoreId != null)
                    item {
                        if (isFromHome == false) {
                            Sizer(10)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Store Banner",
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
                                            CustomColor.primaryColor500, RoundedCornerShape(8.dp)
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

                    when (banners.value == null && storeData != null) {
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
                                    isSendingData.value = true
                                    coroutine.launch {
                                        val result = async {
                                            bannerViewModel.deleteBanner(it)
                                        }.await()

                                        isSendingData.value = false
                                        var errorMessage = ""
                                        errorMessage = if (result.isNullOrEmpty()) {
                                            "banner deleted Seccesffuly"
                                        } else {
                                            result
                                        }
                                        snackBarHostState.showSnackbar(errorMessage)
                                    }
                                })

                        }
                    }

                    Sizer(10)
                }

                item {
                    Sizer(10)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {

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
                                keyboardController?.hide()
                                requestPermissionThenNavigate.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                    )
                                )

                            }) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.location_address_list),
                                "",
                                modifier = Modifier.size(24.dp),
                                tint = CustomColor.primaryColor700
                            )
                        }
                    }
//                        }
//                    }
                }

                item {

                    AnimatedVisibility(
                        visible = (storeId != null || storeData != null)
                    ) {


                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Sizer(20)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Sub Category ",
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
                                            CustomColor.primaryColor500,
                                            RoundedCornerShape(8.dp)
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
                            Sizer(5)
                            LazyRow {
                                when ((subcategories.value == null)) {
                                    true -> {
                                        items(20, key = { key -> key }) {
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
                                                                    selectedSubCategoryId.value =
                                                                        null
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
                                            items(
                                                storeSubCategories,
                                                key = { category -> category.id }) { subCategroy ->
                                                Box(
                                                    modifier = Modifier
                                                        .padding(end = 4.dp)
                                                        .height(40.dp)
//                                                        .width(70.dp)
                                                        .background(
                                                            if (selectedSubCategoryId.value == subCategroy.id
                                                            ) CustomColor.alertColor_3_300 else Color.White,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (selectedSubCategoryId.value == subCategroy.id
                                                            ) Color.White else CustomColor.neutralColor200,
                                                            RoundedCornerShape(8.dp)

                                                        )
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .combinedClickable(onClick = {
                                                            coroutine.launch {
                                                                if (selectedSubCategoryId.value != subCategroy.id) {
                                                                    isChangeSubCategory.value = true

                                                                    selectedSubCategoryId.value =
                                                                        subCategroy.id

                                                                    isChangeSubCategory.value =
                                                                        false
                                                                }

                                                            }

                                                        }, onLongClick = {
                                                            if (isFromHome == false) {

                                                                val name =
                                                                    categories.value?.firstOrNull { it.id == subCategroy.categoryId }?.name
                                                                        ?: ""

                                                                selectedSubCategoryIdHolder.value =
                                                                    subCategroy.id
                                                                categoryName.value =
                                                                    TextFieldValue(name)
                                                                subCategoryName.value =
                                                                    TextFieldValue(
                                                                        subCategroy.name
                                                                    )
                                                                isUpdated.value = true
                                                                isShownSubCategoryBottomSheet.value =
                                                                    true

                                                            }
                                                        })
                                                        .padding(horizontal = 10.dp),
                                                    contentAlignment = Alignment.Center

                                                ) {
                                                    Text(
                                                        subCategroy.name,
                                                        fontFamily = General.satoshiFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (18).sp,
                                                        color = if (selectedSubCategoryId.value == subCategroy.id
                                                        ) Color.White else CustomColor.neutralColor200,

                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                            }

                                        }

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
                                                ProductLoading()
                                            }

                                            else -> {
                                                if (productFilterBySubCategory.isNotEmpty()) {
                                                    ProductShape(
                                                        isCanNavigateToStore = false,
                                                        product = productFilterBySubCategory,
                                                        nav = nav,
                                                        delFun = if (isFromHome == true) null else { it ->
                                                            coroutine.launch {
                                                                isSendingData.value = true
                                                                val result =
                                                                    productViewModel.deleteProduct(
                                                                        storeId!!,
                                                                        it
                                                                    )

                                                                isSendingData.value = false
                                                                var resultMessage = ""
                                                                resultMessage = result
                                                                    ?: "Product is Deleted successfully"

                                                                snackBarHostState.showSnackbar(
                                                                    resultMessage
                                                                )
                                                            }
                                                        },
                                                        updFun = if (isFromHome == true) null else { it ->
                                                            nav.navigate(
                                                                Screens.CreateProduct(
                                                                    storeId.toString(),
                                                                    it.toString()
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
                item{
                    Sizer(50)
                }
                if (!isLoadingMore.value)
                    item {
                        Sizer(140)
                    }

            }

        }
    }


}