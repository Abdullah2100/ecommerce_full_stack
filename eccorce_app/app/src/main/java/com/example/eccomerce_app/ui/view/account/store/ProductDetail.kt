package com.example.eccomerce_app.ui.view.account.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.model.CardProductModel
import com.example.eccomerce_app.model.ProductVarient
import com.example.eccomerce_app.ui.component.Sizer
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.HomeViewModel
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetail(
    nav: NavHostController,
    homeViewModel: HomeViewModel,
    productID: String?,
    isFromHome: Boolean
) {
    val myInfo = homeViewModel.myInfo.collectAsState()
    val product_id = if (productID == null) null else UUID.fromString(productID)

    val products = homeViewModel.products.collectAsState()
    val varients = homeViewModel.varients.collectAsState()
    val productData = products.value?.firstOrNull { it.id == product_id }
    val context = LocalContext.current

    val selectedImage = remember { mutableStateOf(productData?.thmbnail) }
    val selectedProdcutVarients = remember { mutableStateOf<List<ProductVarient>>(emptyList()) }
    if (selectedProdcutVarients.value.isEmpty() && productData?.productVarients?.isNotEmpty() == true) {
        productData.productVarients?.forEach { it ->
            val firstElement = it.first()
            val copySelectedList = mutableListOf<ProductVarient>()
            copySelectedList.add(
                ProductVarient(
                    id = firstElement.id,
                    name = firstElement.name,
                    precentage = firstElement.precentage,
                    varient_id = firstElement.varient_id
                )
            )
            if (selectedProdcutVarients.value.isNotEmpty())
                copySelectedList.addAll(selectedProdcutVarients.value)
            selectedProdcutVarients.value = copySelectedList
        }
    }
    val images = remember { mutableStateOf(productData?.productImages) }

    val coroutine = rememberCoroutineScope()

    if (productData?.thmbnail != null) {
        if (images.value != null && !images.value!!.contains(productData.thmbnail)) {

            var imageWithThumbnails = mutableListOf<String>()
            imageWithThumbnails.add(productData.thmbnail)

            if (images.value != null) {
                imageWithThumbnails.addAll(images.value!!)
            }

            images.value = imageWithThumbnails

        } else if (images.value == null) {
            images.value = listOf<String>(productData.thmbnail)
        }
    }


    val snackbarHostState = remember { SnackbarHostState() }


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
                        "Product Detail",
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
            //  if(isFromHome &&(myInfo.value==null||(myInfo.value!=null&&myInfo.value?.store_id!=productData?.store_id)) )
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    onClick = {
                        homeViewModel.addToCart(
                            product = CardProductModel(
                                id = UUID.randomUUID(),
                                productId = productData!!.id,
                                name = productData.name,
                                thmbnail = productData.thmbnail,
                                price = productData.price,
                                productvarients = selectedProdcutVarients.value,
                                store_id = productData.store_id
                            )
                        )
                        coroutine.launch {
                            snackbarHostState.showSnackbar("Item Added to Cart")

                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomColor.primaryColor400
                    ),

                    ) {


                    Text(
                        "Add To Cart",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (16).sp
                    )


                }

            }
        }

    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = it.calculateTopPadding() + 30.dp)
        ) {

            item {
                SubcomposeAsyncImage(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                      ,
                    model = General.handlingImageForCoil(
                        selectedImage.value,
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

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start

                ) {
                    if (images.value != null && images.value!!.size >= 2) {
                        Sizer(10)
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,

                        ) {
                            items(images.value?.size ?: 0) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(end = 5.dp)
                                        .border(
                                            1.dp,
                                            if (images.value!![index] == selectedImage.value)
                                                CustomColor.primaryColor700 else CustomColor.neutralColor200,
                                            RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    SubcomposeAsyncImage(
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(50.dp)
                                            .width(50.dp)
                                            .clip(
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                if (images.value!![index] != selectedImage.value)
                                                    selectedImage.value = images.value!![index]
                                            },
                                        model = General.handlingImageForCoil(
                                            images.value!![index],
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

                    }
                    Sizer(10)
                    Text(
                        text = productData?.name ?: "",
                        color = CustomColor.neutralColor950,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (18).sp
                    )
                    Sizer(16)
                    Text(
                        text = "\$${productData?.price}",
                        color = CustomColor.neutralColor950,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Sizer(16)
                    Text(
                        text = "Product Details",
                        color = CustomColor.neutralColor950,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Sizer(10)
                    Text(
                        text = productData?.description ?: "",
                        color = CustomColor.neutralColor800,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                    Sizer(15)
                }
            }

            if (!productData?.productVarients.isNullOrEmpty()) {
                items(productData.productVarients?.size ?: 0) { index ->
                    val title =
                        varients.value?.firstOrNull { it.id == productData.productVarients!![index][0].varient_id }?.name
                            ?: ""
                    Text(
                        text = "Select ${title}",
                        color = CustomColor.neutralColor950,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Sizer(10)
                    FlowRow(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        repeat(productData.productVarients!![index].size) { pvIndex ->

                            var productVarientHolder = ProductVarient(
                                id = productData.productVarients!![index][pvIndex].id,
                                name = productData.productVarients!![index][pvIndex].name,
                                precentage = productData.productVarients!![index][pvIndex].precentage,
                                varient_id = productData.productVarients!![index][pvIndex].varient_id
                            )
                            when (title == "Color") {
                                true -> {
                                    val colorValue =
                                        General.convertColorToInt(productData.productVarients!![index][pvIndex].name)

                                    if (colorValue != null)
                                        Box(
                                            modifier = Modifier
                                                .height(24.dp)
                                                .width(24.dp)
                                                .background(
                                                    colorValue,
                                                    RoundedCornerShape(20.dp)
                                                )
                                                .border(
                                                    width = if (selectedProdcutVarients.value.contains(
                                                            productVarientHolder
                                                        )
                                                    ) 1.dp else 0.dp,
                                                    color = if (selectedProdcutVarients.value.contains(
                                                            productVarientHolder
                                                        )
                                                    ) CustomColor.primaryColor700
                                                    else Color.Transparent,
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .clip(RoundedCornerShape(20.dp))
                                                .clickable {

                                                    val copyVarient =
                                                        mutableListOf<ProductVarient>()

                                                    copyVarient.add(productVarientHolder)
                                                    copyVarient.addAll(selectedProdcutVarients.value)
                                                    selectedProdcutVarients.value =
                                                        copyVarient.distinctBy { it.varient_id }

                                                }
                                                .padding(5.dp)
                                        )
                                }

                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .border(
                                                1.dp,
                                                if (selectedProdcutVarients.value.contains(
                                                        productVarientHolder
                                                    )
                                                ) CustomColor.primaryColor700 else CustomColor.neutralColor200,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 10.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .clickable {

                                                val copyVarient = mutableListOf<ProductVarient>()

                                                copyVarient.add(productVarientHolder)
                                                copyVarient.addAll(selectedProdcutVarients.value)
                                                selectedProdcutVarients.value = copyVarient
                                                    .distinctBy { it.varient_id }

                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = productData.productVarients!![index][pvIndex].name,
                                            color = CustomColor.neutralColor950,
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            // modifier = Modifier.padding(start = 15.dp)
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
            }

        }
    }

}