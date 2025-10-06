package com.example.eccomerce_app.ui.view.ReseatPassword

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.CustomButton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.AuthViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@Composable
fun ReseatPasswordScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel,
    email: String,
    otp: String
) {


    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutine = rememberCoroutineScope()


    val snackBarHostState = remember { SnackbarHostState() }

    val newPassword = remember { mutableStateOf(TextFieldValue("")) }


    val isSendingData = remember { mutableStateOf(false) }



    Scaffold(snackbarHost = { SnackbarHost(hostState = snackBarHostState) })
    {

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
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    ImageVector.vectorResource(R.drawable.password_icon),
                    "",
                    tint = CustomColor.primaryColor700,
                    modifier = Modifier.size(200.dp)
                )
                Sizer(heigh = 5)

                TextInputWithTitle(
                    newPassword,
                    title = "",
                    placeHolder = "Enter Your New Password",
                )



                CustomButton(
                    isLoading = isSendingData.value,
                    operation = {
                        keyboardController?.hide()
                        isSendingData.value = true
                        coroutine.launch {
                            val result = async {
                                authViewModel.reseatPassword(email, otp, newPassword.value.text)
                            }.await()
                            isSendingData.value = false

                            if (!result.isNullOrEmpty()) {
                                snackBarHostState.showSnackbar(result)
                            } else {
                                nav.navigate(Screens.LocationGraph) {
                                    popUpTo(nav.graph.id) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    },
                    isEnable = newPassword.value.text.trim().isNotEmpty(),
                    buttonTitle = "Update",

                    )

            }
        }


    }

}