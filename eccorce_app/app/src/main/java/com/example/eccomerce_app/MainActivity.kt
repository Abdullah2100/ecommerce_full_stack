package com.example.eccomerce_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.ui.NavController
import com.example.eccomerce_app.ui.theme.Eccomerce_appTheme
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.Util.General
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    var keepSplash = true;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplash
            }
        }

        enableEdgeToEdge()
        setContent {
            val nav = rememberNavController();
            val authViewModle: AuthViewModel = koinViewModel();
            val isPassingOnBoarding = remember { mutableStateOf<Boolean?>(null) }
            val isLogin = remember { mutableStateOf<Boolean?>(null) }
            val result = authViewModle.isPassingOnBoardinScreen.collectAsState()
            val resultLogin = authViewModle.isLogin.collectAsState()



            if (result.value!=null) {
                isPassingOnBoarding.value=result.value
                isLogin.value = resultLogin.value
                keepSplash = false;
            }
            if(isPassingOnBoarding.value!=null&&isLogin.value!=null)
                NavController(nav, isPassingOnBoarding.value == true, isLogin.value == true)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "${General.BASED_URL}",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Eccomerce_appTheme {
        Greeting("Android")
    }
}