package com.example.e_commerc_delivery_man.ui.component

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.util.General
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.OrderViewModel
import com.example.eccomerce_app.model.Order
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID


@Composable
fun OrderComponent(
    order: Order,
    isCancel: Boolean = false,
    isSendingData: MutableState<Boolean>,
    selectedId: MutableState<UUID>,
    screenWidth: Int,
    snackBarHostState: SnackbarHostState,
    orderViewModel: OrderViewModel,
    requestPermission: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
) {
    val context = LocalContext.current

    val coroutine = rememberCoroutineScope()

    val isExpanded = remember { mutableStateOf(false) }

    val rotation = animateFloatAsState(
        if (isExpanded.value) 180f else 0f
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .shadow(8.dp)
            .background(
                Color.White,
                RoundedCornerShape(8.dp)
            )

            .padding(horizontal = 5.dp, vertical = 10.dp)

    )
    {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    order.name,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center
                )

                Text(
                    order.userPhone,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center

                )

                Row {
                    Text(
                        "OrderItems :",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )

                    Text(
                        "${order.orderItems.size}",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )
                }


                Row {
                    Text(
                        "Total Price : ",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )

                    Text(
                        "${order.totalPrice}",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )
                }

                Row {
                    Text(
                        "DeliveryFee : ",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )

                    Text(
                        "${order.deliveryFee}",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )
                }

                Row {
                    Text(
                        "Order Status : ",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )

                    Text(
                        order.status,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center

                    )
                }


            }

            IconButton({
                selectedId.value = order.id
                requestPermission.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }) {
                Icon(
                    ImageVector.vectorResource(R.drawable.location),
                    "",
                    tint = CustomColor.primaryColor500
                )
            }
        }
        if (isCancel)
            AnimatedVisibility(
                visible = isExpanded.value,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween()
                ) + fadeIn(),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween()
                ) + fadeOut()

            )
            {

                order.orderItems
                    .groupBy { it.product?.storeId ?: UUID.randomUUID() }
                    .values
                    .forEach { it ->
                        it.forEach { orderItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                SubcomposeAsyncImage(
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .height(80.dp)
                                        .width(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    model = General.handlingImageForCoil(
                                        orderItems.product?.thmbnail,
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
                                        orderItems.product?.name ?: "",
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Sizer(width = 5)
                                    orderItems.productVarient?.forEach { value ->

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                value.varient_name + ": ",
                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = (16).sp,
                                                color = CustomColor.neutralColor950,
                                                textAlign = TextAlign.Center
                                            )
                                            when (value.varient_name == "Color") {
                                                true -> {
                                                    val colorValue =
                                                        General.convertColorToInt(
                                                            value.product_varient_name
                                                        )

                                                    if (colorValue != null)
                                                        Box(
                                                            modifier = Modifier
                                                                .height(20.dp)
                                                                .width(20.dp)
                                                                .background(
                                                                    colorValue,
                                                                    RoundedCornerShape(
                                                                        20.dp
                                                                    )
                                                                )

                                                                .clip(
                                                                    RoundedCornerShape(
                                                                        20.dp
                                                                    )
                                                                )
                                                            //                                                    .padding(5.dp)
                                                        )
                                                }

                                                else -> {
                                                    Box(
                                                        modifier = Modifier

                                                            .clip(
                                                                RoundedCornerShape(
                                                                    20.dp
                                                                )
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = value.product_varient_name,
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Status : ",
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = (16).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            orderItems.orderItemStatus,
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
        if (isCancel)
            Row(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .clickable {
                        isExpanded.value = !isExpanded.value
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text("Show Items")
                Sizer(width = 5)
                Icon(
                    Icons.Default.KeyboardArrowDown, "",
                    modifier = Modifier.rotate(rotation.value)
                )
            }
        Box(
            Modifier
                .padding(top = 10.dp)
                .width(((screenWidth) - 20).dp)
        ) {
            CustomBotton(

                buttonTitle = if (isCancel) "Cancel Order" else "Accept Order",
                operation = {
                    coroutine.launch {
                        isSendingData.value = true;
                        val result = async {
                            when (isCancel) {
                                true -> orderViewModel.cancelOrder(order.id)
                                else -> orderViewModel.takeOrder(order.id)
                            }
                        }.await()
                        isSendingData.value = false
                        if (!result.isNullOrEmpty()) {
                            snackBarHostState
                                .showSnackbar(result)
                        }
                        orderViewModel.getMyOrders(mutableStateOf(1))
                    }

                },
                color = if (!isCancel) CustomColor.alertColor_2_700
                else CustomColor.alertColor_1_600,
//                                        isLoading = isSendingData.value && deletedId.value == order.id
            )
        }
    }

}