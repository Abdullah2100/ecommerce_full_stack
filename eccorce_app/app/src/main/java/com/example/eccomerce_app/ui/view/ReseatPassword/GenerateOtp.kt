package com.example.e_commercompose.ui.view.ReseatPassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.CustomBotton
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.viewModel.AuthViewModel
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.theme.CustomColor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@Composable
fun GenerateOtpScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel
){

    val fontScall = LocalDensity.current.fontScale

    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    val email = remember { mutableStateOf(TextFieldValue("")) }


    val isSendingData = remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()



    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {

        it.calculateTopPadding()
        it.calculateBottomPadding()


        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
                .background(Color.White)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                   ,
                horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
            ) {


                Icon(
                    ImageVector.vectorResource(R.drawable.mail_validation),
                    "",
                    tint = CustomColor.primaryColor700,
                    modifier = Modifier.size(200.dp))

                TextInputWithTitle(
                    email,
                    title = "",
                    placHolder = "Enter Your email",
                )




                CustomBotton(
                    isLoading = isSendingData.value,
                    operation = {
                        keyboardController?.hide()
                        isSendingData.value=true
                        coroutine.launch {
                            val result = async {
                                authViewModel.getOtp(email.value.text)
                            }.await()
                            isSendingData.value=false

                            if(!result.isNullOrEmpty())
                            {
                                snackbarHostState.showSnackbar(result)
                            }
                            else{
                                nav.navigate(Screens.OtpVerification(email = email.value.text))
                                email.value = TextFieldValue("")
                            }
                        }
                    },
                    isEnable = email.value.text.trim().isNotEmpty(),
                    buttonTitle = "Check",

                )

            }
        }


    }

}