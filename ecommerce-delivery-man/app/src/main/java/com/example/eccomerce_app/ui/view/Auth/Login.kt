package com.example.e_commerc_delivery_man.ui.view.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.e_commerc_delivery_man.ui.component.CustomAuthBotton
import com.example.e_commerc_delivery_man.ui.component.Sizer
import com.example.e_commerc_delivery_man.ui.component.TextInputWithTitle
import com.example.e_commerc_delivery_man.ui.component.TextSecureInputWithTitle
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    nav: NavHostController,
    authKoin: AuthViewModel
) {
    val fontScall = LocalDensity.current.fontScale

    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    val userNameOrEmail = remember { mutableStateOf(TextFieldValue("ali@gmail.com")) }
    val password = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }

    val isEmailError = remember { mutableStateOf<Boolean>(false) }
    val isPasswordError = remember { mutableStateOf<Boolean>(false) }
    val erroMessage = remember { mutableStateOf("") }

    val isSendingData = remember { mutableStateOf(false) }
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
                    .padding(top = 50.dp)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.Start,
            ) {

                Text(
                    "Login",
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    color = CustomColor.neutralColor950,
                    fontSize = (44 / fontScall).sp
                )

                Sizer(heigh = 30)
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

                Sizer(heigh = 30)


                CustomAuthBotton(
                    isLoading = isSendingData.value,
                    operation = {
                        keyboardController?.hide()

                        coroutine.launch {
                            if(userNameOrEmail.value.text.isBlank()||password.value.text.isBlank())
                            {
                                snackbarHostState.showSnackbar("User name Or Password is Blanck")
                            }
                            else{
                                isSendingData.value = true
                                val result = async {
                                    authKoin.loginUser(
                                        userNameOrEmail.value.text, password = password.value.text,
                                    )


                                }.await()

                                isSendingData.value = false

                                if (!result.isNullOrEmpty()) {
                                    snackbarHostState.showSnackbar(result)
                                } else {

                                }

                            }

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


//        }
//    )


}