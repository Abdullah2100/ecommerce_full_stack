package com.example.e_commerc_delivery_man.ui.view.Auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.viewModel.AuthViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.CustomAuthBotton
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.component.TextInputWithTitle
import com.example.e_commerc_delivery_man.ui.component.TextSecureInputWithTitle
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel
) {
    val fontScall = LocalDensity.current.fontScale

    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    val userNameOrEmail = remember { mutableStateOf(TextFieldValue("ali@gmail.com")) }
    val password = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }

    val isEmailError = remember { mutableStateOf<Boolean>(false) }
    val isPasswordError = remember { mutableStateOf<Boolean>(false) }
    val erroMessage = remember { mutableStateOf("") }

    val isSendingData = authViewModel.isLoading.collectAsState()
    val errorMessage = authViewModel.errorMesssage.collectAsState()
    val coroutine = rememberCoroutineScope()


    val focusRequester = FocusRequester()

    fun validateLoginInput(
        username: String,
        password: String
    ): Boolean {

        isEmailError.value = false;
        isPasswordError.value = false;

        if (username.trim().isEmpty()) {
            erroMessage.value = "email must not be empty"
            isEmailError.value = true;
            return false;
        } else if (password.trim().isEmpty()) {
            erroMessage.value = ("password must not be empty")
            isPasswordError.value = true
            return false;
        }

        return true
    }


    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            coroutine.launch {
                snackbarHostState.showSnackbar(errorMessage.value.toString())
                authViewModel.clearErrorMessage()
            }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {

        it.calculateTopPadding()
        it.calculateBottomPadding()


        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .padding(top = it.calculateTopPadding()+50.dp)
                .padding(horizontal = 10.dp)
            ,horizontalAlignment = Alignment.Start,
        ) {

            Text(
                "Login",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                color = CustomColor.neutralColor950,
                fontSize = (34 / fontScall).sp
            )

            Sizer(heigh = 50)
            TextInputWithTitle(
                userNameOrEmail,
                title = "Email",
                placHolder = "Enter Your email",
                isHasError = isEmailError.value,
                erroMessage = erroMessage.value,
                focusRequester = focusRequester
            )
            TextSecureInputWithTitle(
                password,
                "Password",
                isPasswordError.value,
                erroMessage.value
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    "Forget Password",
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Medium,
                    color = CustomColor.neutralColor950,
                    fontSize = (16 / fontScall).sp,
                    modifier = Modifier.clickable {
                        nav.navigate(Screens.ReseatPasswordGraph)
                    }
                )
            }

            Sizer(heigh = 30)


            CustomAuthBotton(
                isLoading = isSendingData.value,
                operation = {
                    keyboardController?.hide()

                    if (userNameOrEmail.value.text.isBlank() || password.value.text.isBlank()) {
                        coroutine.launch {
                            snackbarHostState.showSnackbar("User name Or Password is Blanck")
                        }
                    } else {

                        authViewModel.loginUser(
                            userNameOrEmail.value.text,
                            password = password.value.text,
                            nav = nav
                        )


                    }

                },
                buttonTitle = "Login",
                validationFun = {
                    validateLoginInput(
                        username = userNameOrEmail.value.text,
                        password = password.value.text
                    )

                }
            )

        }



    }



}