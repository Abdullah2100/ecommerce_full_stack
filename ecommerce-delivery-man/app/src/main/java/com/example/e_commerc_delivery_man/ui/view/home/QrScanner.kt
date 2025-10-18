package com.example.e_commerc_delivery_man.ui.view.home

import android.text.Layout
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.viewModel.OrderViewModel
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.e_commerc_delivery_man.services.kSerializeChanger.ImageAnalyser
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.eccomerce_app.model.OrderItem
import com.google.android.libraries.navigation.internal.aft.qr
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@Composable
fun QrScannerPage(
    nav: NavHostController,
    orderViewModel: OrderViewModel,
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    val coroutine = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }


    val isOpenDialog = remember { mutableStateOf(true) }
    val isSendingData = remember { mutableStateOf(false) }

    fun updateStatus(id: String) {
        isOpenDialog.value = true

        coroutine.launch {
            isSendingData.value = true
            val result = async {
                orderViewModel.updateStatus(id)
            }.await()
            isSendingData.value = false
            if (!result.isNullOrEmpty()) {
                isOpenDialog.value=false
                snackBarHostState.showSnackbar(result)

                return@launch;
            }
            nav.popBackStack();

        }

    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    ) { paddingValues ->
        paddingValues.calculateTopPadding()

        paddingValues.calculateBottomPadding()

        if (isOpenDialog.value)
            Dialog(
                onDismissRequest = { isOpenDialog.value = !isOpenDialog.value }
            ) {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(70.dp)
                        .background(Color.White,RoundedCornerShape(20.dp))
                   , contentAlignment = Alignment.Center

                ) {
                    CircularProgressIndicator()
                }
            }

        AndroidView(
            factory = { context ->
                val camerProvider = ProcessCameraProvider.getInstance(context)
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                preview.surfaceProvider = previewView.surfaceProvider

                val imageAnalyser = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                if (!isOpenDialog.value)
                    imageAnalyser.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        ImageAnalyser { qrCode ->
                            updateStatus(qrCode)
                        }
                    )

                try {
                    camerProvider.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalyser
                    )
                } catch (e: Exception) {

                }

                previewView
            },
            modifier = Modifier.fillMaxWidth()
        )


    }
}