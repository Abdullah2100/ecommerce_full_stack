package com.example.eccomerce_app.ui.component

import android.util.Log
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.model.Banner
import com.example.eccomerce_app.ui.Screens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


@Composable
fun BannerBage(

    banners: List<Banner>,
    isMe: Boolean? = false,
    nav: NavHostController? = null
) {

    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    val currentPage = remember { mutableStateOf(0) }
    var pagerState = rememberPagerState { banners.size }

    var operationType = remember { mutableStateOf('+') }

    if (banners.size > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                pagerState.animateScrollToPage(currentPage.value, animationSpec = TweenSpec(
                    1000,
                    easing = EaseInOut
                ))

                if (currentPage.value== banners.size-1 ) {
                    operationType.value='-';
                } else if(currentPage.value==-1) {
                    operationType.value='+';
                }

                when(operationType.value){
                    '-'->{
                        currentPage.value-=1;
                    }
                    else->{
                        currentPage.value+=1;
                    }
                }
                delay(3000)
            }
        }
    }




    HorizontalPager(
        modifier = Modifier.padding(top = 15.dp), state = pagerState,
        pageSpacing = 2.dp
    ) { page ->
        SubcomposeAsyncImage(
            contentScale = ContentScale.Crop,
            modifier =
                if (isMe == true) Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)) else
                    Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            Log.d("navBannerIsPresing", "true")
                            nav!!.navigate(Screens.Store(banners[page].store_id.toString()))
                        },
            model = General.handlingImageForCoil(
                banners[page].image,
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
                        modifier = Modifier.size(54.dp) // Adjust the size here
                    )
                }
            },
        )
    }

}