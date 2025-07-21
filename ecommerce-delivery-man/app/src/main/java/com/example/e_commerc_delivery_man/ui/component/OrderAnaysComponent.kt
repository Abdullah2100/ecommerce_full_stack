package com.example.e_commerc_delivery_man.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.theme.CustomColor


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun OrdersAnalys(
    couter:Int,
    name :String,
    analysType:String
    ){
   val conig= LocalConfiguration.current;
    val sceenWidth = conig.screenWidthDp

    Column(
        modifier= Modifier
            .width(width =((sceenWidth/2)-20).dp)
            .background(CustomColor.alertColor_2_500,
                RoundedCornerShape(16.dp))
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            couter.toString(),
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (50).sp,
            color = Color.White,
            textAlign = TextAlign.Start
        )
        Sizer(9)

        Text(
            name,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (20).sp,
            color = Color.White,
            textAlign = TextAlign.Start
        )
        Sizer(5)

        Text(
            analysType,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (16).sp,
            color = Color.White,
            textAlign = TextAlign.Start
        )
    }



}