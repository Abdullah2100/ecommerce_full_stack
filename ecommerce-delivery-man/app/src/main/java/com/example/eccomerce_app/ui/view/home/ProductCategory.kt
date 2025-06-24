package com.example.e_commerc_delivery_man.ui.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.BannerBage
import com.example.e_commerc_delivery_man.ui.component.CategoryLoadingShape
import com.example.e_commerc_delivery_man.ui.component.CategoryShape
import com.example.e_commerc_delivery_man.ui.component.LocationLoadingShape
import com.example.e_commerc_delivery_man.ui.component.ProductLoading
import com.example.e_commerc_delivery_man.ui.component.ProductShape
import com.example.e_commerc_delivery_man.Util.General.reachedBottom
import kotlinx.coroutines.delay
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ProductCategoryScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel,
    category_id:String
) {
    val categoryId = UUID.fromString(category_id)
    val categories = homeViewModel.categories.collectAsState()
    val products = homeViewModel.products.collectAsState()
    val productsByCateogry = products.value?.filter { it.category_id==categoryId }

    val lazyState = rememberLazyListState()
    val reachedBottom = remember {
        derivedStateOf {
            lazyState.reachedBottom() // Custom extension function to check if the user has reached the bottom
        }
    }
    var page = remember { mutableStateOf(1) }


    val isLoadingMore = remember { mutableStateOf(false) }


    LaunchedEffect(reachedBottom.value) {
        if(!productsByCateogry.isNullOrEmpty() && reachedBottom.value){
            Log.d("scrollReachToBotton","true")
            homeViewModel.getProductsByCategoryID(
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
                        categories.value?.firstOrNull { it.id==categoryId }?.name?:"" ,
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
        }


    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()


        LazyColumn(
            state = lazyState,
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 15.dp)
//                .padding(top = 50.dp)

        ) {


            item {

                Sizer(10)
                when (productsByCateogry == null) {
                    true -> {
                        ProductLoading(50)
                    }

                    else -> {
                        if (productsByCateogry.isNotEmpty()) {
                            ProductShape(products.value!!, nav = nav)
                        }
                    }
                }
            }

            if (isLoadingMore.value) {
                item {
                    Box(modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth(),
                        contentAlignment = Alignment.Center)
                    {
                        CircularProgressIndicator(color = CustomColor.primaryColor700)
                    }
                }
            }

            item{
                Sizer(40)
            }
        }


    }
}