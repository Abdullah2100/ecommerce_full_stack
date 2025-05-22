package com.example.eccomerce_app.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.ui.theme.CustomColor


@Composable
fun CustomAuthBotton(
    isLoading: Boolean,
    operation: () -> Unit,
    validationFun: () -> Boolean,
    buttonTitle: String
){
    val fontScall = LocalDensity.current.fontScale

    Button(
        enabled = isLoading==false,
        modifier = Modifier
            .padding(bottom = 50.dp)
            .height(50.dp)
            .fillMaxWidth(),
        onClick = {

            if (
                validationFun()
            )
                operation()
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CustomColor.primaryColor400
        ),

        ) {

        when (isLoading) {
            true -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(30.dp)
                )

            }

            else -> {

                Text(
                    buttonTitle,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (16 / fontScall).sp
                )
            }
        }
    }

}


@Composable
fun CustomBotton(
    isLoading: Boolean=false,
    operation: () -> Unit,
    buttonTitle: String,
    color: Color?=null,
    isEnable: Boolean?=true
){
    val fontScall = LocalDensity.current.fontScale

    Button(
        enabled = isEnable!!,
        modifier = Modifier
//            .padding(bottom = 50.dp)
            .height(50.dp)
            .fillMaxWidth(),
        onClick = {

                operation()
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor =color?: CustomColor.primaryColor400
        ),

        ) {
        when (isLoading) {
            true -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(30.dp)
                )

            }

            else -> {

                Text(
                    buttonTitle,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (16 / fontScall).sp
                )
            }
        }


    }

}


@Composable
fun CustomTitleBotton(
    operation: () -> Unit,
    buttonTitle: String,
    color: Color?
){
    val fontScall = LocalDensity.current.fontScale

    TextButton (
        modifier = Modifier
//            .padding(bottom = 50.dp)
            .height(50.dp)
            .fillMaxWidth(),
        onClick = {

            operation()
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor =Color.Transparent//color?: CustomColor.primaryColor400
           // ,
        ),

        ) {



        Text(
            buttonTitle,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (16 / fontScall).sp,
            color = color?: CustomColor.primaryColor700
        )
    }

}