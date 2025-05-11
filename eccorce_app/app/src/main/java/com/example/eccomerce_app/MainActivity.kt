package com.example.eccomerce_app

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.eccomerce_app.ui.NavController
import com.example.eccomerce_app.ui.theme.Eccomerce_appTheme
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.Util.General
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    var keepSplash = true;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = (this as Activity).window
//        window.statusBarColor= Color.White.toArgb()
//        WindowCompat.getInsetsController(window, this)
//            .isAppearanceLightStatusBars = !darkTheme
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



            if (currentScreen.value!=null) {
                keepSplash = false;
            }
            if(currentScreen.value!=null)
                NavController(nav, currentScreen=currentScreen.value?:1)
        }
    }
}