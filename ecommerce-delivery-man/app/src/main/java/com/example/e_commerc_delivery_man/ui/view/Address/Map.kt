package com.example.e_commerc_delivery_man.ui.view.Address

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel



@Composable
fun MapScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {


    Scaffold {
        it.calculateTopPadding()
        it.calculateBottomPadding()

    }

}