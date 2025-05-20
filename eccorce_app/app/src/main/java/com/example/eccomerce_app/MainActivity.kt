package com.example.eccomerce_app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.dto.response.BannerResponseDto
import com.example.eccomerce_app.model.Banner
import com.example.eccomerce_app.ui.NavController
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.model.ButtonNavItem
import com.example.eccomerce_app.model.DtoToModel.toBanner
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.util.Secrets
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    var keepSplash = true;
//    private  var connection: HubConnection?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = (this as Activity).window

       /* val connectionUrl = Secrets.getBaseUrl().replace("/api","")+"/bannerHub"
         connection = HubConnectionBuilder
             .create(connectionUrl)
            .withTransport(com.microsoft.signalr.TransportEnum.LONG_POLLING)
            .build()

        // Start the connection
        connection?.start()?.blockingAwait()

        // Register a handler for incoming messages
        connection?.on("createdBanner",
            {
                    result->
                var banners= mutableListOf<Banner>();
                if(General.banners.value==null){
                   banners.add(result.toBanner())
                }
                else{
                    banners.add(result.toBanner())
                    banners.addAll(General.banners.value!!)
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    General.banners.emit(banners)
                }
            },
            BannerResponseDto::class.java).runCatching {

        }*/



        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplash
            }
        }

        enableEdgeToEdge()
        setContent {
            val nav = rememberNavController();
            val authViewModle: AuthViewModel = koinViewModel();
            val currentScreen = authViewModle.currentScreen.collectAsState()
            var buttonNavItemHolder = listOf<ButtonNavItem>(
                ButtonNavItem(name ="Home", imageId = Icons.Outlined.Home,0 ),
                ButtonNavItem(name ="Saved", imageId = Icons.Outlined.Favorite,1 ),
                ButtonNavItem(name ="Cart", imageId = Icons.Outlined.ShoppingCart,2 ),
                ButtonNavItem(name ="Account", imageId = Icons.Outlined.Person ,3),
            )

            val pages = listOf(
                Screens.Home,
                Screens.Home,
                Screens.Home,
                Screens.Account,
            )



            if (currentScreen.value!=null) {
                keepSplash = false;
            }
            if(currentScreen.value!=null)

                Scaffold(
                    bottomBar = {
                        val navBackStackEntry = nav.currentBackStackEntryAsState()
                        if(
                            navBackStackEntry.value?.destination?.hasRoute(Screens.Home::class) == true||
                            navBackStackEntry.value?.destination?.hasRoute(Screens.Account::class) == true
                            )
                        {
                            Column {
                                Box(modifier=Modifier
                                    .fillMaxWidth()
                                    .height(0.2.dp
                                    ).background(CustomColor.neutralColor200))
                                NavigationBar(
                                    modifier = Modifier.background(Color.White),
                                    tonalElevation = 5.dp,
                                    containerColor = Color.White,
                                    content = {
                                        buttonNavItemHolder.fastForEachIndexed { index, value ->
                                            var isSelectedItem = navBackStackEntry.value?.destination?.hasRoute(
                                                pages[index]::class)==true;
                                            NavigationBarItem(
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = CustomColor.primaryColor700,
                                                    unselectedIconColor =CustomColor.neutralColor600,
                                                    selectedTextColor = CustomColor.primaryColor700,
                                                    unselectedTextColor =CustomColor.neutralColor600,
                                                ),

                                                selected =isSelectedItem,
                                                onClick = {
                                                if (!isSelectedItem)
                                                    nav.navigate(pages[index])
                                                },
                                                label = {
                                                    Text(
                                                        text = value.name,
                                                        fontFamily = General.satoshiFamily,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 12.sp,
                                                        textAlign = TextAlign.Center
                                                    )
                                                },
                                                icon = {
                                                    Icon(
                                                        imageVector = value.imageId,
                                                        contentDescription = "",
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                })
                                        }

                                    }
                                )
                            }
                        }
                    }
                )
            {
                    it.calculateTopPadding()
                    it.calculateBottomPadding()
                    NavController(nav, currentScreen=currentScreen.value?:1)
                }
        }
    }


    override fun onDestroy() {
//        connection?.stop()
        super.onDestroy()
    }
}