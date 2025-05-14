package com.example.eccomerce_app.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.model.Category
import com.example.eccomerce_app.ui.theme.CustomColor


@Composable
fun CategoryShape(
    categories:List<Category>
){
    Log.d("category_image",categories[0].image)
    var context = LocalContext.current



    LazyRow(modifier = Modifier.padding(top = 10.dp)) {
        items(count = categories.size) { index->
            Column(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable{},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .height(69.dp)
                        .width(70.dp)
                        .background(CustomColor.primaryColor50, RoundedCornerShape(8.dp))
                        .padding(horizontal = 5.dp)
                ){
                    SubcomposeAsyncImage(
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .height(69.dp)
                            .width(70.dp),
                        model = General.handlingImageForCoil(categories[index].image,context) ,
                        contentDescription = "",
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                            ) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(35.dp),
                                    strokeWidth = 2.dp// Adjust the size here
                                )
                            }
                        },
                        onError = {error->
                            Log.d("errorFromPlaingImage",error.result.toString())
                        }
                    )

                }
                Sizer(4)
                Text(categories[index].name,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = CustomColor.neutralColor900,
                )
            }
        }
    }

}