package com.example.e_commerc_delivery_man.ui.component

import android.util.Log
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_commerc_delivery_man.ui.theme.CustomColor


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


@Composable
fun ProductLoading(size:Int){
    FlowRow (
        modifier = Modifier

            .fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
          repeat (size) {
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