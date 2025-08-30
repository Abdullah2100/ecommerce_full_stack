package com.example.eccomerce_app.ui.component

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.model.OrderItem
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General


@Composable
fun OrderItemCartShape(orderItem: OrderItem, context: Context){
    Row {
        SubcomposeAsyncImage(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            model = General.handlingImageForCoil(
                orderItem.product.thumbnail,
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

        Sizer(width = 5)

        Column {
            Text(
                orderItem.product.name,
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (16).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Start
            )
            if (orderItem.productVariant.isNotEmpty())
                Sizer(5)

            orderItem.productVariant.forEach { variant ->

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        variant.variantName + " :",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (16).sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center
                    )
                    Sizer(width = 5)
                    when (variant.variantName == "Color") {
                        true -> {
                            val colorValue =
                                General.convertColorToInt(variant.productVariantName)

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
                                    text = variant.productVariantName,
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
                    "Status  :",
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (16).sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center
                )
                Sizer(width = 5)
                Text(
                    text = orderItem.orderItemStatus,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16).sp,
                    color = when (orderItem.orderItemStatus) {
                        "InProgress" -> CustomColor.alertColor_3_500
                        "Excepted" -> CustomColor.alertColor_2_700
                        else -> CustomColor.alertColor_2_700
                    },
                    textAlign = TextAlign.Center
                )


            }
        }
    }

}

@Composable
fun OrderItemForMyStoreShape(
    orderItem: OrderItem,
    context: Context,
    screenWidth:Int){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )

    {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .wrapContentHeight()
                .width((screenWidth).dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = General.handlingImageForCoil(
                    orderItem.product.thumbnail,
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
            Column {
                Text(
                    orderItem.product.name,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = (16).sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Sizer(width = 5)
                orderItem.productVariant.forEach { value ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val title = value.variantName
                        Text(
                            "$title :",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )
                        Sizer(width = 5)
                        when (title == "Color") {
                            true -> {
                                val colorValue =
                                    General.convertColorToInt(value.productVariantName)

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
                                        text = value.productVariantName,
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

                Row {
                    Text(
                        "Quantity :",

                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16).sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center
                    )
                    Sizer(width = 5)
                    Text(
                        "${orderItem.quantity}",

                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16).sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }


    }

}