package com.example.eccomerce_app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eccomerce_app.ui.theme.CustomColor


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


@Composable
fun CategoryLoadingShape(length: Int = 1) {

    LazyRow(modifier = Modifier.padding(top = 10.dp)) {
        items(count = length) {
            Column(
                modifier = Modifier.padding(end = 5.dp)
            ) {
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