package com.example.e_commerc_delivery_man.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun Sizer(heigh:Int=0,width:Int=0){
    Box(
      modifier = Modifier
          .height((heigh).dp)
          .width((width.dp))
    )
}