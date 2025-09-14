package com.example.eccomerce_app.ui.view.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.ui.component.ProductLoading
import com.example.e_commercompose.ui.component.ProductShape
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ProductCategoryScreen(
    nav: NavHostController,
    categoryId: String,
    categoryViewModel: CategoryViewModel,
    productViewModel: ProductViewModel
) {
    val categories = categoryViewModel.categories.collectAsState()
    val products = productViewModel.products.collectAsState()

    val categoryId = UUID.fromString(categoryId)
    val productsByCategory = products.value?.filter { it.categoryId == categoryId }


    val lazyState = rememberLazyListState()


    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }
    val isLoadingMore = remember { mutableStateOf(false) }

    val page = remember { mutableIntStateOf(1) }




    LaunchedEffect(reachedBottom.value) {
        if (!productsByCategory.isNullOrEmpty() && reachedBottom.value && productsByCategory.size>23) {
            productViewModel.getProductsByCategoryID(
                page,
                categoryId,
                isLoadingMore
            )
        }

    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        categories.value?.firstOrNull { it.id == categoryId }?.name ?: "",
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
        }


    ) { scaffoldStatus ->
        scaffoldStatus.calculateTopPadding()
        scaffoldStatus.calculateBottomPadding()


        LazyColumn(
            state = lazyState,
            modifier = Modifier
                .padding(top = scaffoldStatus.calculateTopPadding())
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 15.dp)
        ) {


            item {

                Sizer(10)
                when (productsByCategory == null) {
                    true -> {
                        ProductLoading(50)
                    }

                    else -> {
                        if (productsByCategory.isNotEmpty()) {
                            ProductShape(products.value!!, nav = nav)
                        }
                    }
                }
            }

            if (isLoadingMore.value) {
                item {
                    Box(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    )
                    {
                        CircularProgressIndicator(color = CustomColor.primaryColor700)
                    }
                }
            }

            item {
                Sizer(40)
            }
        }


    }
}