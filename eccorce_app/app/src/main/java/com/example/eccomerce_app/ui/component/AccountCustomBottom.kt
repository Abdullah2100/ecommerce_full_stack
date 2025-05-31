package com.example.e_commercompose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.ui.theme.CustomColor


@Composable
fun AccountCustomBottom(title: String, icon: Int, operation: () -> Unit){
    Column(
        modifier = Modifier
            .padding(bottom = 5.dp, top = 5.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable{
                operation()
            }
    ){
        Row(
            modifier = Modifier
                .padding(top = 5.dp, start = 3.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = ImageVector.vectorResource(icon),"",
                    modifier = Modifier.size(24.dp),
                    tint = CustomColor.neutralColor900)
                Sizer(width = 15)
                Text(
                    title,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center

                )
            }

            Icon(Icons.Default.KeyboardArrowRight,"",
                tint = CustomColor.neutralColor950,
                modifier = Modifier.size(24.dp))
        }

        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .height(1.dp)
                .fillMaxWidth()
                .background(CustomColor.neutralColor200)
        )
    }
}

@Composable
fun LogoutBotton(title: String, icon: Int, operation: () -> Unit){
    Column(
        modifier = Modifier
            .padding(bottom = 15.dp, top = 10.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable{
                operation()
            }

    ){
        Row(
            modifier = Modifier
                .padding(top = 10.dp, start = 2.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = ImageVector.vectorResource(icon),"",
                    modifier = Modifier.size(24.dp),
                    tint = CustomColor.alertColor_1_600)
                Sizer(width = 15)
                Text(
                    title,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = CustomColor.alertColor_1_600,
                    textAlign = TextAlign.Center

                )
            }

        }

        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .height(1.dp)
                .fillMaxWidth()
                .background(CustomColor.neutralColor200)
        )
    }
}