package com.example.eccomerce_app.ui.view.account.store

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.toCustomFil
import com.example.e_commercompose.dto.ModelToDto.toListOfProductVarient
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.SubCategoryViewModel
import com.example.e_commercompose.viewModel.VariantViewModel
import com.example.hotel_mobile.Util.Validation
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    nav: NavHostController,
    storeId: String,
    productId: String? = null,
    subCategoryViewModel: SubCategoryViewModel,
    variantViewModel: VariantViewModel,
    productViewModel: ProductViewModel,

    ) {
    val context = LocalContext.current

    val coroutine = rememberCoroutineScope()

    val storeIdHolder = UUID.fromString(storeId)
    val productIdHolder = if (productId == null) null else UUID.fromString(productId)


    val products = productViewModel.products.collectAsState()
    val variants = variantViewModel.variants.collectAsState()

    val subCategory = subCategoryViewModel.subCategories.collectAsState()
    val storeSubCategory = subCategory.value?.filter { it.storeId == storeIdHolder }


    val productData =
        if (productIdHolder == null) null else products.value?.firstOrNull { it -> it.id == productIdHolder }


    val thumbnail = remember { mutableStateOf(productData?.thumbnail) }


    val images = remember { mutableStateOf(productData?.productImages ?: emptyList()) }
    val productVariants =
        remember { mutableStateOf(if (productData != null && !productData.productVariants.isNullOrEmpty()) productData.productVariants.toListOfProductVarient() else emptyList()) }
    val deleteImages = remember { mutableStateOf<List<String>>(emptyList()) }
    val deleteProductVariant =
        remember { mutableStateOf<List<ProductVarientSelection>>(emptyList()) }


    val productName = remember { mutableStateOf(TextFieldValue("")) }
    val description = remember { mutableStateOf(TextFieldValue("")) }
    val price = remember { mutableStateOf(TextFieldValue("")) }
    val productVariantName = remember { mutableStateOf(TextFieldValue("")) }
    val productVariantPercentage = remember { mutableStateOf(TextFieldValue("")) }


    val selectedSubCategoryId = remember { mutableStateOf<UUID?>(null) }
    val selectedVariantId = remember { mutableStateOf<UUID?>(null) }


    val isExpandedSubCategory = remember { mutableStateOf(false) }
    val isExpandedVariant = remember { mutableStateOf(false) }
    val isSendingData = remember { mutableStateOf(false) }


    val animated = animateDpAsState(
        if (isExpandedSubCategory.value) ((storeSubCategory?.size ?: 1) * 45).dp else 0.dp
    )
    val rotation = animateFloatAsState(if (isExpandedSubCategory.value) 180f else 0f)
    val animatedVariant = animateDpAsState(
        if (isExpandedVariant.value) ((variants.value?.size ?: 1) * 45).dp else 0.dp
    )
    val rotationVariant = animateFloatAsState(if (isExpandedVariant.value) 180f else 0f)

    val snackBarHostState = remember { SnackbarHostState() }


    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    )
    { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context)
            if (fileHolder != null) {
                if (thumbnail.value != null && !deleteImages.value.contains(thumbnail.value)) {
                    val deleteImageCopy = mutableListOf<String>()
                    deleteImageCopy.add(thumbnail.value!!)
                    deleteImageCopy.addAll(deleteImages.value)
                    deleteImages.value = deleteImageCopy
                }
                thumbnail.value = fileHolder.absolutePath

            }
        }
    }


    val selectMultipleImages = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    )
    { uris ->
        val imagesHolder = mutableListOf<String>()

        if (uris.isNotEmpty()) {
            uris.forEach { productImages ->
                val file = productImages.toCustomFil(context)

                if (file != null) {
                    imagesHolder.add(file.absolutePath)
                }
            }
            if (imagesHolder.isNotEmpty()) {
                images.value = imagesHolder
            }
        }
    }


    fun validateInput(): Boolean {
        var errorMessage = ""
        if (thumbnail.value == null) {
            errorMessage = "product thumbnail is require"
        } else if (images.value.isEmpty())
            errorMessage = "you must select least one image for product"
        else if (productName.value.text.trim().isEmpty())
            errorMessage = "product name is require"
        else if (description.value.text.trim().isEmpty())
            errorMessage = "product description is required"
        else if (price.value.text.trim().isEmpty())
            errorMessage = "product price is required"
        else if (selectedSubCategoryId.value == null)
            errorMessage = "you must select subCategory"

        if (errorMessage.isNotEmpty()) {
            coroutine.launch {
                snackBarHostState.showSnackbar(errorMessage)
            }
            return false
        }
        return true
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                        if (productId == null) "Create Product" else "Update Product",
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
            )
        },

        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                CustomBotton(
                    isLoading = isSendingData.value,
                    operation = {

                        if (productIdHolder == null) {
                            val validationResult = validateInput()
                            if (validationResult) {
                                coroutine.launch {
                                    isSendingData.value = true
                                    val result = async {
                                        productViewModel.createProducts(
                                            name = productName.value.text,
                                            description = description.value.text,
                                            thmbnail = File(thumbnail.value!!),
                                            subcategoryId = selectedSubCategoryId.value!!,
                                            store_id = storeIdHolder!!,
                                            price = price.value.text.toDouble(),
                                            productVariants = productVariants.value,
                                            images = images.value.map { it -> File(it) },
                                        )
                                    }.await()
                                    isSendingData.value = false
                                    if (!result.isNullOrEmpty()) {
                                        snackBarHostState.showSnackbar(result)
                                    } else {
                                        thumbnail.value = null
                                        images.value = emptyList()
                                        productName.value = TextFieldValue("")
                                        price.value = TextFieldValue("")
                                        description.value = TextFieldValue("")
                                        selectedSubCategoryId.value = null
                                        productVariants.value = emptyList()
                                        snackBarHostState.showSnackbar("Product created Successfully")
                                        nav.popBackStack()
                                    }

                                }
                            }

                        } else {
                            val newProductVariant = mutableListOf<ProductVarientSelection>()
                            if (productVariants.value.isNotEmpty()) {
                                newProductVariant.addAll(productVariants.value)
                            }
                            if (newProductVariant.isNotEmpty() && (productData != null && !productData.productVariants.isNullOrEmpty())) {
                                newProductVariant.removeAll(productData.productVariants.toListOfProductVarient())
                            }

                            val newImages = mutableListOf<String>()
                            if (images.value.isNotEmpty()) {
                                newImages.addAll(images.value)
                            }
                            if (newImages.isNotEmpty() && (productData != null && productData.productImages.isNotEmpty())) {
                                newImages.removeAll(productData.productImages)
                            }


                            coroutine.launch {
                                isSendingData.value = true
                                val result = async {
                                    productViewModel.updateProducts(
                                        id = productIdHolder,
                                        name = productName.value.text.ifEmpty { null },
                                        description = description.value.text.ifEmpty { null },
                                        thmbnail = if (thumbnail.value != productData?.thumbnail) File(
                                            thumbnail.value!!
                                        ) else null,
                                        subcategoryId = if (selectedSubCategoryId.value == null) null else selectedSubCategoryId.value!!,
                                        storeId = storeIdHolder!!,
                                        price = if (price.value.text.isEmpty()) null
                                        else if (Validation.isValidMoney(price.value.text)) price.value.text.toDouble()
                                        else null,
                                        productVariants = if (newProductVariant.isEmpty()) null
                                        else newProductVariant,
                                        images = if (newImages.isEmpty()) null else newImages.map { it ->
                                            File(
                                                it
                                            )
                                        }.toList(),
                                        deletedImages = deleteImages.value.ifEmpty { null },
                                        deletedProductVarients = deleteProductVariant.value.ifEmpty { null }
                                    )
                                }.await()
                                isSendingData.value = false
                                if (!result.isNullOrEmpty()) {
                                    snackBarHostState.showSnackbar(result)
                                } else {
                                    thumbnail.value = null
                                    images.value = emptyList()
                                    productName.value = TextFieldValue("")
                                    price.value = TextFieldValue("")
                                    description.value = TextFieldValue("")
                                    selectedSubCategoryId.value = null
                                    productVariants.value = emptyList()
                                    snackBarHostState.showSnackbar("Product Update Successfully")
                                    nav.popBackStack()
                                }
                            }
                        }
                    },
                    buttonTitle = if (productIdHolder != null) "Update Product" else "Create Product",
                    isEnable = true,
                )
            }
        }
    ) { scaffoldStatus ->
        scaffoldStatus.calculateTopPadding()
        scaffoldStatus.calculateBottomPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 15.dp)
                .padding(top = scaffoldStatus.calculateTopPadding() + 30.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                "Product Thumbnail",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(15)
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
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
                            color = CustomColor.neutralColor500,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = Color.White,
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (thumbnail.value == null) {
                        true -> {
                            Icon(
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
//                                                .padding(top = 35.dp)
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                model = General.handlingImageForCoil(
                                    thumbnail.value,
                                    context
                                ),
                                contentDescription = "",
                                loading = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
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
                Box(
                    modifier = Modifier
                        .padding(end = 5.dp, bottom = 10.dp)
                        .constrainAs(cameralRef) {
                            end.linkTo(imageRef.end)
                            bottom.linkTo(imageRef.bottom)
                        }


                ) {

                    IconButton(
                        onClick = {
//                          keyboardController?.hide()
//                          isPigImage.value = true
                            onImageSelection.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .size(30.dp),
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
            Sizer(30)
            Text(
                "Product Images",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(15)
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val (cameralRef) = createRefs()

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = CustomColor.neutralColor500,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(5.dp)

                ) {
                    if (images.value.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                                "",
                                modifier = Modifier.size(80.dp),
                                tint = CustomColor.neutralColor200
                            )
                        }
                    }

                    images.value.forEachIndexed { index, value ->

                        Log.d("productImageIs", value)
                        ConstraintLayout {
                            Box(
                                modifier = Modifier

                                    .height(120.dp)
                                    .width(120.dp)
                                    .background(
                                        color = Color.White,
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp)),
                                    model = General.handlingImageForCoil(
                                        value,
                                        context
                                    ),
                                    contentDescription = "",
                                    loading = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(),
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

                            Box(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(30.dp)
                                    .background(
                                        Color.Red,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable {
                                        if (productData != null && productData.productImages.contains(
                                                value
                                            )
                                        ) {
                                            val deleteImageList = mutableListOf<String>()
                                            deleteImageList.add(value)
                                            deleteImageList.addAll(deleteImages.value)
                                            deleteImages.value = deleteImageList
                                        }
                                        images.value = images.value.filter { it -> it != value }

                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Clear, "",
                                    tint = Color.White
                                )
                            }
                        }

                    }
                }


                Box(
                    modifier = Modifier
                        .padding(end = 5.dp, bottom = 10.dp)
                        .constrainAs(cameralRef) {
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }


                ) {

                    IconButton(
                        onClick = {
                            selectMultipleImages.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .size(30.dp),
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
            Sizer(30)
            Text(
                "Name",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            TextInputWithTitle(
                value = productName,
                title = "",
                placHolder = productData?.name ?: "Product Name"
            )

            Text(
                "Price",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(2)
            OutlinedTextField(

                maxLines = 6,
                value = price.value,
                onValueChange = { price.value = it },
                placeholder = {
                    Text(
                        productData?.price?.toString() ?: "Product Price",
                        color = CustomColor.neutralColor500,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16).sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Black
                ),
                textStyle = TextStyle(
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16).sp,
                    color = CustomColor.neutralColor950
                ),
                trailingIcon = {
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),

                )
            Sizer(20)
            Text(
                "Description",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(2)
            OutlinedTextField(

                maxLines = 6,
                value = description.value,
                onValueChange = { description.value = it },
                placeholder = {
                    Text(
                        productData?.description ?: "Product Description",
                        color = CustomColor.neutralColor500,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16).sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Black
                ),
                textStyle = TextStyle(
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16).sp,
                    color = CustomColor.neutralColor950
                ),
                trailingIcon = {
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )

            Sizer(10)

            Text(
                "SubCategory",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(5)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        CustomColor.neutralColor400,
                        RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {

                Row(
                    modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth()

                        .clickable {
                            isExpandedSubCategory.value = !isExpandedSubCategory.value
                        }
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        if (productData != null && selectedSubCategoryId.value == null)
                            storeSubCategory?.firstOrNull { it.id == productData.subcategoryId }?.name
                                ?: "Select SubCategory "
                        else if (selectedSubCategoryId.value == null) "Select SubCategory "
                        else storeSubCategory?.firstOrNull { it.id == selectedSubCategoryId.value }?.name
                            ?: ""
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown, "",
                        modifier = Modifier.rotate(rotation.value)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(animated.value)
                        .border(
                            1.dp,
                            CustomColor.neutralColor200,
                            RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        ),

                    ) {
                    storeSubCategory?.forEachIndexed { index, value ->
                        Text(
                            value.name,
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    isExpandedSubCategory.value = false
                                    selectedSubCategoryId.value = value.id
                                }
                                .padding(top = 12.dp, start = 5.dp)

                        )
                    }
                }
            }
            Sizer(5)
            Text(
                "Product Variant",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(2)
            if (productVariants.value.isNotEmpty())
                Sizer(5)
            FlowRow {
                productVariants.value.forEachIndexed { index, value ->
                    ConstraintLayout(
                        modifier = Modifier.padding(end = 5.dp, bottom = 10.dp)
                    ) {
                        val (iconRef) = createRefs()
                        Column(
                            modifier = Modifier
                                .background(
                                    CustomColor.alertColor_3_300,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    end = 25.dp,
                                    start = 5.dp
                                )
                        ) {
                            Text(
                                variants.value?.firstOrNull { it.id == value.variantId }?.name
                                    ?: "",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (18).sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                value.name,
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (18).sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .width(20.dp)
                                .background(
                                    Color.Red,
                                    RoundedCornerShape(20.dp)
                                )
                                .clip(
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (productData != null && !productData.productVariants.isNullOrEmpty() &&
                                        productData.productVariants.toListOfProductVarient()
                                            .contains(value)
                                    ) {
                                        val deletedProductVariant =
                                            mutableListOf<ProductVarientSelection>()
                                        deletedProductVariant.add(value)
                                        deletedProductVariant.addAll(deleteProductVariant.value)
                                        deleteProductVariant.value = deletedProductVariant
                                    }
                                    productVariants.value =
                                        productVariants.value.filter { it.name != value.name }
                                }
                                .constrainAs(iconRef) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Clear, "",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
            if (productVariants.value.isNotEmpty())
                Sizer(5)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        CustomColor.neutralColor400,
                        RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {

                Row(
                    modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth()

                        .clickable {
                            isExpandedVariant.value = !isExpandedVariant.value
                        }
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        if (selectedVariantId.value == null) "Select Variant"
                        else variants.value?.firstOrNull { it.id == selectedVariantId.value }?.name
                            ?: ""
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown, "",
                        modifier = Modifier.rotate(rotationVariant.value)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(animatedVariant.value)
                        .border(
                            1.dp,
                            CustomColor.neutralColor200,
                            RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        ),

                    ) {
                    if (!variants.value.isNullOrEmpty())
                        variants.value!!.forEachIndexed { index, value ->
                            Text(
                                value.name,
                                modifier = Modifier
                                    .height(50.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        isExpandedVariant.value = false
                                        selectedVariantId.value = value.id
                                    }
                                    .padding(top = 12.dp, start = 5.dp)

                            )
                        }
                }
            }
            Sizer(5)


            OutlinedTextField(

                maxLines = 6,
                value = productVariantName.value,
                onValueChange = { productVariantName.value = it },
                placeholder = {
                    Text(
                        "Variant Name",
                        color = CustomColor.neutralColor500,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16).sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Black
                ),
                textStyle = TextStyle(
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16).sp,
                    color = CustomColor.neutralColor950
                ),
                trailingIcon = {
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            Sizer(5)

            OutlinedTextField(

                maxLines = 6,
                value = productVariantPercentage.value,
                onValueChange = { productVariantPercentage.value = it },
                placeholder = {
                    Text(
                        "Variant Price",
                        color = CustomColor.neutralColor500,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16).sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Black
                ),
                textStyle = TextStyle(
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16).sp,
                    color = CustomColor.neutralColor950
                ),
                trailingIcon = {
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            Sizer(5)

            CustomBotton(
                isLoading = false,
                operation = {
                    val selectedVariant = ProductVarientSelection(
                        name = productVariantName.value.text,
                        percentage = if (productVariantPercentage.value.text.isEmpty()) 1.0 else productVariantPercentage.value.text.toDouble(),
                        variantId = selectedVariantId.value!!
                    )

                    val productVariantHolder = mutableListOf<ProductVarientSelection>()
                    productVariantHolder.addAll(productVariants.value)
                    productVariantHolder.add(selectedVariant)
                    productVariants.value = productVariantHolder

                    productVariantName.value = TextFieldValue("")
                    productVariantPercentage.value = TextFieldValue("")
                    selectedVariantId.value = null
                },
                buttonTitle = "Add ProductVariant",
                isEnable = selectedVariantId.value != null && productVariantName.value.text.isNotEmpty(),
                color = null
            )
            Box(modifier = Modifier.height(140.dp))
        }
    }
}
