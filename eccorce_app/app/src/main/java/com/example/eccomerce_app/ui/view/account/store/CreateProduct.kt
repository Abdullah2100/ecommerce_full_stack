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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.compose.runtime.MutableState
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
import com.example.eccomerce_app.R
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.Util.General.toCustomFil
import com.example.eccomerce_app.dto.ModelToDto.toListOfProductVarient
import com.example.eccomerce_app.model.ProductVarientSelection
import com.example.eccomerce_app.ui.component.CustomBotton
import com.example.eccomerce_app.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.HomeViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel,
    storeId: String,
    productId: String? = null
) {
    val products = homeViewModel.products.collectAsState()
    val store_id = UUID.fromString(storeId)
    val product_id = if (productId == null) null else UUID.fromString(productId)
    var productData =
        if (product_id == null) null else products.value?.firstOrNull { it -> it.id == product_id }
    val context = LocalContext.current

    val subCategory = homeViewModel.subCategories.collectAsState()
    val storeSubCategory = subCategory.value?.filter { it.store_id == store_id }

    val snackbarHostState = remember { SnackbarHostState() }

    val thumbnail =
        remember { mutableStateOf<String?>(if (productData != null) productData.thmbnail else null) }
    val images =
        remember { mutableStateOf<List<String>>(if (productData != null) productData.productImages else emptyList<String>()) }

    val productName = remember { mutableStateOf(TextFieldValue("")) }
    val description = remember { mutableStateOf(TextFieldValue("")) }
    val price = remember { mutableStateOf(TextFieldValue("")) }
    val selectedSubCategoryId = remember { mutableStateOf<UUID?>(null) }

    var isExpandedSubCategory = remember { mutableStateOf(false) }

    var varients = homeViewModel.varients.collectAsState()
    val productVarients = remember {
        mutableStateOf<List<ProductVarientSelection>>(
            if (productData != null && !productData.productVarients.isNullOrEmpty())
                productData.productVarients!!.toListOfProductVarient()
            else emptyList()
        )
    }
    val prodcutVarientName = remember { mutableStateOf(TextFieldValue("")) }
    val prodcutVarientPrecentage = remember { mutableStateOf(TextFieldValue("")) }
    val selectedVarientId = remember { mutableStateOf<UUID?>(null) }
    var isExpandedVarient = remember { mutableStateOf(false) }


    val deleteImages = remember { mutableStateOf<List<String>>(emptyList()) }
    val deleteProductVarient = remember { mutableStateOf<List<ProductVarientSelection>>(emptyList()) }


    var corotine = rememberCoroutineScope()

    var isSendingData = remember { mutableStateOf(false) }


    var animated = animateDpAsState(
        if (isExpandedSubCategory.value) ((storeSubCategory?.size ?: 1) * 45).dp else 0.dp
    )

    var rotation = animateFloatAsState(
        if (isExpandedSubCategory.value) 180f else 0f
    )


    var animatedVarient = animateDpAsState(
        if (isExpandedVarient.value) ((varients.value?.size ?: 1) * 45).dp else 0.dp
    )

    var rotationVarient = animateFloatAsState(
        if (isExpandedVarient.value) 180f else 0f
    )
    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context);
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


    val selectMutipleImages = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uris ->
        val imagesHolder = mutableListOf<String>()

        if (uris.isNotEmpty()) {
            uris.forEach { productImages ->
                val file = productImages.toCustomFil(context)
                if (images.value.isNotEmpty()) {
                    imagesHolder.addAll(images.value)

                }
                if (file != null) {
                    imagesHolder.add(file.absolutePath)
                }
                if (imagesHolder.isNotEmpty()) {
                    images.value = imagesHolder
                }

            }
        }
    }

    Log.d("productData", "${productData.toString()}")

    fun validateInut(): Boolean {
        var errorMessage = "";
        if (thumbnail.value == null) {
            errorMessage = "product thumbnail is require"
        } else if (images.value.isEmpty())
            errorMessage = "you must select atleast one image for product"
        else if (productName.value.text.trim().isEmpty())
            errorMessage = "product name is require"
        else if (description.value.text.trim().isEmpty())
            errorMessage = "product description is required"
        else if (price.value.text.trim().isEmpty())
            errorMessage = "product price is required"
        else if (selectedSubCategoryId.value == null)
            errorMessage = "you must select subCategory"

        if (errorMessage.isNotEmpty()) {
            corotine.launch {
                snackbarHostState.showSnackbar(errorMessage)
            }
            return false
        }
        return true
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
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
                            Icons.Default.KeyboardArrowLeft,
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

                        if (product_id == null) {
                            var validationResult = validateInut()
                            if (validationResult) {
                                corotine.launch {
                                    isSendingData.value = true;
                                    val result = async {
                                        homeViewModel.createProducts(
                                            name = productName.value.text,
                                            description = description.value.text,
                                            thmbnail = File(thumbnail.value!!),
                                            subcategory_id = selectedSubCategoryId.value!!,
                                            store_id = store_id!!,
                                            price = price.value.text.toDouble(),
                                            productVarients = productVarients.value,
                                            images = images.value.map { it -> File(it) },
                                        )
                                    }.await()
                                    isSendingData.value = false
                                    if (!result.isNullOrEmpty()) {
                                        snackbarHostState.showSnackbar(result)
                                    } else {
                                        thumbnail.value = null
                                        images.value = emptyList<String>()
                                        productName.value = TextFieldValue("")
                                        price.value = TextFieldValue("")
                                        description.value = TextFieldValue("")
                                        selectedSubCategoryId.value = null;
                                        productVarients.value = emptyList<ProductVarientSelection>()
                                        snackbarHostState.showSnackbar("Product created Scucessfuly")
                                        nav.popBackStack()
                                    }

                                }
                            }

                        } else {
                           var newProductVarient = mutableListOf<ProductVarientSelection>();
                            if(productVarients.value.isNotEmpty()){
                                newProductVarient.addAll(productVarients.value)
                            }
                           if(newProductVarient.isNotEmpty()&&(productData!=null&&!productData.productVarients.isNullOrEmpty())){
                              newProductVarient.removeAll(productData.productVarients!!.toListOfProductVarient())
                           }

                            var newImages = mutableListOf<String>();
                            if(images.value.isNotEmpty()){
                                newImages.addAll(images.value)
                            }
                            if(newImages.isNotEmpty()&&(productData!=null&&!productData.productImages.isNullOrEmpty())){
                                newImages.removeAll(productData.productImages)
                            }


                            corotine.launch {
                                isSendingData.value = true;
                                val result = async {
                                    homeViewModel.updateProducts(
                                        id = product_id,
                                        name = if (productName.value.text.isEmpty()) null else productName.value.text,
                                        description = if (description.value.text.isEmpty()) null else description.value.text,
                                        thmbnail = if (thumbnail.value != productData?.thmbnail) File(
                                            thumbnail.value!!
                                        ) else null,
                                        subcategory_id = if (selectedSubCategoryId.value == null) null else selectedSubCategoryId.value!!,
                                        store_id = store_id!!,
                                        price = if (price.value.text.isEmpty()) null
                                        else if (General.isValideMony(price.value.text.toString())) price.value.text.toDouble()
                                        else null,
                                        productVarients = if (newProductVarient.isEmpty()) null
                                        else newProductVarient,
                                        images =if(newImages.isEmpty())null else newImages.map { it -> File(it) }.toList(),
                                        deletedimages = if(deleteImages.value.isEmpty())null else deleteImages.value,
                                        deletedProductVarients = if(deleteProductVarient.value.isEmpty()) null else deleteProductVarient.value
                                    )
                                }.await()
                                isSendingData.value = false
                                if (!result.isNullOrEmpty()) {
                                    snackbarHostState.showSnackbar(result)
                                } else {
                                    thumbnail.value = null
                                    images.value = emptyList<String>()
                                    productName.value = TextFieldValue("")
                                    price.value = TextFieldValue("")
                                    description.value = TextFieldValue("")
                                    selectedSubCategoryId.value = null;
                                    productVarients.value = emptyList<ProductVarientSelection>()
                                    snackbarHostState.showSnackbar("Product Update Scucessfuly")
                                    nav.popBackStack()
                                }

                            }

                        }
                    },
                    buttonTitle =if(product_id!=null) "Update Product" else  "Create Product",
                    isEnable = true,
                )
            }
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 15.dp)
                .padding(top = it.calculateTopPadding() + 30.dp)
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
//                          isPigImage.value = true;
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
                            var (image, icon) = createRefs()

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
                                            val deletImageList = mutableListOf<String>()
                                            deletImageList.add(value)
                                            deletImageList.addAll(deleteImages.value)
                                            deleteImages.value = deletImageList
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
                            selectMutipleImages.launch(
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
                placHolder = if (productData != null) productData.name else "Product Name"
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
                onValueChange = {
                    //if (General.isValideMony(it.text))
                    price.value = it
                },
                placeholder = {
                    Text(
                        if (productData != null) productData.price.toString() else
                            "Product Price",
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
                        if (productData != null) productData.description.toString() else "Product Description",
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
            ) {

                Row(
                    modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            CustomColor.neutralColor400,
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
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
                            storeSubCategory?.firstOrNull { it.id == productData.subcategory_id }?.name
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
                        .padding(bottom = 19.dp)
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
                    if (storeSubCategory != null)
                        storeSubCategory.forEachIndexed { index, value ->
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
                "Product Varient",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(2)
            if (productVarients.value.isNotEmpty())
                Sizer(5)
            FlowRow {
                productVarients.value.forEachIndexed { index, value ->
                    ConstraintLayout {
                        var (iconRef) = createRefs()
                        Column(
                            modifier = Modifier
                                .background(
                                    CustomColor.alertColor_3_300,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 5.dp
                                )
                        ) {
                            Text(
                                varients.value?.firstOrNull { it.id == value.varient_id }?.name
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
                                    if (productData != null && !productData.productVarients.isNullOrEmpty()&&
                                        productData.productVarients!!.toListOfProductVarient().contains(value)) {
                                        var deletedProductVarient = mutableListOf<ProductVarientSelection>()
                                        deletedProductVarient.add(value)
                                        deletedProductVarient.addAll(deleteProductVarient.value)
                                        deleteProductVarient.value = deletedProductVarient
                                    }
                                    productVarients.value =
                                        productVarients.value.filter { it.name != value.name }
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
            if (productVarients.value.isNotEmpty())
                Sizer(5)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            CustomColor.neutralColor400,
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            isExpandedVarient.value = !isExpandedVarient.value
                        }
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        if (selectedVarientId.value == null) "Select Variant"
                        else varients.value?.firstOrNull { it.id == selectedVarientId.value }?.name
                            ?: ""
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown, "",
                        modifier = Modifier.rotate(rotationVarient.value)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(animatedVarient.value)
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
                    if (!varients.value.isNullOrEmpty())
                        varients.value!!.forEachIndexed { index, value ->
                            Text(
                                value.name,
                                modifier = Modifier
                                    .height(50.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        isExpandedVarient.value = false
                                        selectedVarientId.value = value.id
                                    }
                                    .padding(top = 12.dp, start = 5.dp)

                            )
                        }
                }
            }
            Sizer(5)


            OutlinedTextField(

                maxLines = 6,
                value = prodcutVarientName.value,
                onValueChange = { prodcutVarientName.value = it },
                placeholder = {
                    Text(
                        "Varient Name",
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
                value = prodcutVarientPrecentage.value,
                onValueChange = { prodcutVarientPrecentage.value = it },
                placeholder = {
                    Text(
                        "Varient Price",
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
                    val selectedVarient = ProductVarientSelection(
                        name = prodcutVarientName.value.text,
                        precentage = if (prodcutVarientPrecentage.value.text.isEmpty()) 1.0 else prodcutVarientPrecentage.value.text.toDouble(),
                        varient_id = selectedVarientId.value!!
                    )

                    var productVarientHolder = mutableListOf<ProductVarientSelection>()
                    productVarientHolder.addAll(productVarients.value)
                    productVarientHolder.add(selectedVarient)
                    productVarients.value = productVarientHolder

                    prodcutVarientName.value = TextFieldValue("")
                    prodcutVarientPrecentage.value = TextFieldValue("")
                    selectedVarientId.value = null
                },
                buttonTitle = "Add ProductVarient",
                isEnable = selectedVarientId.value != null && prodcutVarientName.value.text.isNotEmpty(),
                color = null
            )
            Box(modifier = Modifier.height(140.dp))
        }
    }
}
