package com.example.e_commercompose.ui.view.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.viewModel.HomeViewModel
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.CustomBotton
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {

    var cardData = homeViewModel.cartImes.collectAsState()
    var varient = homeViewModel.varients.collectAsState()
    var context = LocalContext.current
    var config = LocalConfiguration.current;
    var screenWidth = config.screenWidthDp
    val snackbarHostState = remember { SnackbarHostState() }
    Log.d("cartItemsLength", "${cardData.value.cartProducts.size}")



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
                        "My Cart",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (24).sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center
                    )
                },

                )
        },
        floatingActionButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 65.dp)
                    .offset(x= 16.dp)


                ,
            ) {
                if(cardData.value.totalPrice!=null&&cardData.value.totalPrice!=0.0){
                        Row(
                            modifier = Modifier
                                .background(Color.White)
                                .padding(top = 15.dp)
                                .padding(horizontal = 15.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "\$${cardData.value.totalPrice}",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                        }

                     Sizer(30)
                       Box(
                           modifier = Modifier
                               .background(Color.White)
                               .padding(horizontal = 15.dp)
                       ){
                           CustomBotton(
                               buttonTitle = "Go to Checkout",
                               operation = {
                                   nav.navigate(Screens.Checkout)
                               }
                           )
                       }
                }

            }
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cardData.value.cartProducts.size) { index ->
                SwappToDismiss(
                    component = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                                ,
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .width((screenWidth - 120).dp),

                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SubcomposeAsyncImage(
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(80.dp)
                                            .width(80.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        model = General.handlingImageForCoil(
                                            cardData.value.cartProducts[index].thmbnail,
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
                                                    modifier = Modifier.size(53.dp) // Adjust the size here
                                                )
                                            }
                                        },
                                    )
                                    Sizer(width = 10)
                                    Column(
                                    ) {
                                        Text(
                                            cardData.value.cartProducts[index].name,
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = (16).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Sizer(width = 5)
                                        cardData.value.cartProducts[index].productvarients.forEach { value ->

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val title =
                                                    varient.value?.firstOrNull { it.id == value.varient_id }?.name
                                                        ?: ""
                                                Text(
                                                    (varient.value?.firstOrNull { it.id == value.varient_id }?.name
                                                        ?: "") + ": ",
                                                    fontFamily = General.satoshiFamily,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = (16).sp,
                                                    color = CustomColor.neutralColor950,
                                                    textAlign = TextAlign.Center
                                                )
                                                when (title == "Color") {
                                                    true -> {
                                                        val colorValue =
                                                            General.convertColorToInt(value.name)

                                                        if (colorValue != null)
                                                            Box(
                                                                modifier = Modifier
                                                                    .height(20.dp)
                                                                    .width(20.dp)
                                                                    .background(
                                                                        colorValue,
                                                                        RoundedCornerShape(20.dp)
                                                                    )

                                                                    .clip(RoundedCornerShape(20.dp))
//                                                    .padding(5.dp)
                                                            )
                                                    }

                                                    else -> {
                                                        Box(
                                                            modifier = Modifier

                                                                .clip(RoundedCornerShape(20.dp)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = value.name,
                                                                fontFamily = General.satoshiFamily,
                                                                fontWeight = FontWeight.Normal,
                                                                fontSize = (16).sp,
                                                                color = CustomColor.neutralColor800,
                                                                textAlign = TextAlign.Center
                                                            )
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }

                                }


                                Row(
                                    modifier = Modifier.width(90.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(25.dp)
                                            .background(
                                                CustomColor.neutralColor200,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                homeViewModel.decreaseCardItem(cardData.value.cartProducts[index].id)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            ImageVector.vectorResource(R.drawable.baseline_horizontal),
                                            "",
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        "${cardData.value.cartProducts[index].quantity}",
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (24).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(25.dp)
                                            .background(
                                                CustomColor.primaryColor700,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                homeViewModel.increaseCardItem(cardData.value.cartProducts[index].id)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Add, "",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .height(1.dp)
                                    .fillMaxWidth()
                                    .background(CustomColor.neutralColor200)
                            )
                        }

                    },
                    index = cardData.value.cartProducts[index].id,
                    delete = { index ->
                        homeViewModel.removeItemFromCard(index)
                    }

                )
            }

            item{
                Box(modifier = Modifier.height(190.dp))
            }
              }
    }
}


@Composable
fun SwappToDismiss(
    component: @Composable () -> Unit,
    index: UUID,
    delete: (id: UUID) -> Unit
) {

    val swipeState = rememberSwipeToDismissBoxState()

    LaunchedEffect(swipeState.currentValue) {
        if (swipeState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            delete(index)
        }
    }
    SwipeToDismissBox(
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        state = swipeState,
        content = {
            component()
        },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .background(CustomColor.alertColor_1_400),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Default.Delete, "",
                        tint = CustomColor.alertColor_1_600,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        gesturesEnabled = true,

        )
}
