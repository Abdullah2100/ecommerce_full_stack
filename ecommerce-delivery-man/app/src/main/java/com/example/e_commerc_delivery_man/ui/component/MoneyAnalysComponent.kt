package com.example.e_commerc_delivery_man.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commerc_delivery_man.util.General


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MonayAnalys(
    mony: Double,
    type:String,
    ){
   val conig= LocalConfiguration.current;
    val width = conig.screenWidthDp

    Row {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(((width / 3) - 32).dp)

        ) {
            Text(
                type,
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (16).sp,
                color = Color.White,
                textAlign = TextAlign.Start
            )
            Sizer(5)
            Text(
                String.format("%.0f", mony),
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (17).sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                softWrap = true
            )
        }


    }

}