package com.example.e_commercompose.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.e_commercompose.ui.theme.CustomColor


@Composable
fun LocationLoadingShape(width: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.height(80.dp)
    ) {
        Column(
            modifier = Modifier
                .width((width - 30 - 34).dp)
        ) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(120.dp)
                    .background(CustomColor.primaryColor50, RoundedCornerShape(5.dp))
            )
            Sizer(4)
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(120.dp)
                    .background(CustomColor.primaryColor50, RoundedCornerShape(5.dp))
            )
        }

        Box(
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
                .background(CustomColor.primaryColor50, RoundedCornerShape(5.dp))
        )
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CategoryLoadingShape() {

    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp.dp

    val items = ((screenWidth - 30.dp) / 75)



    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {

        Box(
            modifier = Modifier
                .height(15.dp)
                .width(60.dp)
                .background(CustomColor.primaryColor50)
        )
        Sizer(4)
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(70.dp)
                .background(CustomColor.primaryColor50)
        )
    }
    LazyRow(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        items(count = items.value.toInt()) {
            Column() {
                Box(
                    modifier = Modifier
                        .height(69.dp)
                        .width(70.dp)
                        .background(CustomColor.primaryColor50)
                )
                Sizer(4)
                Box(
                    modifier = Modifier
                        .height(15.dp)
                        .width(70.dp)
                        .background(CustomColor.primaryColor50)
                )
            }
        }
    }

}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun BannerLoading() {

    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {

            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(60.dp)
                    .background(CustomColor.primaryColor50)
            )

        }
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .height(150.dp)
                .fillMaxWidth()
                .background(
                    CustomColor.primaryColor50,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }

}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ProductLoading(length: Int? = null) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp.dp

    val items = ((screenWidth - 30.dp) / 160)

    FlowRow(
        modifier = Modifier

            .fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(length ?: items.value.toInt()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .width(160.dp)
                        .background(
                            CustomColor.primaryColor50,
                            RoundedCornerShape(8.dp)
                        )
                )
                Sizer(10)
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(140.dp)
                        .background(
                            CustomColor.primaryColor50,
                            RoundedCornerShape(8.dp)
                        )
                )
                Sizer(10)
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(50.dp)
                        .background(
                            CustomColor.primaryColor50,
                            RoundedCornerShape(8.dp)
                        )
                )
            }
        }
    }
}