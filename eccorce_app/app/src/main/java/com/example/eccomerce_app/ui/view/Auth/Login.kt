package com.example.eccomerce_app.ui.view.Auth

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
import com.example.eccomerce_app.viewModel.AuthViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.component.TextSecureInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    nav: NavHostController, authKoin: AuthViewModel
) {

    val fontScall = LocalDensity.current.fontScale
    val keyboardController = LocalSoftwareKeyboardController.current

    val errorMessage = authKoin.errorMessage.collectAsState()

    val coroutine = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val isSendingData = remember { mutableStateOf(false) }
    val isEmailError = remember { mutableStateOf(false) }
    val isPasswordError = remember { mutableStateOf(false) }

    val userNameOrEmail = remember { mutableStateOf(TextFieldValue("ali@gmail.com")) }
    val password = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }

//    val userNameOrEmail = remember { mutableStateOf(TextFieldValue("")) }
//    val password = remember { mutableStateOf(TextFieldValue("")) }


    val errorMessageValidation = remember { mutableStateOf("") }


    fun validateLoginInput(
        username: String, password: String
    ): Boolean {

        isEmailError.value = false
        isPasswordError.value = false
        when {
            username.trim().isEmpty() -> {
                errorMessageValidation.value = "email must not be empty"
                isEmailError.value = true
                return false
            }

            password.trim().isEmpty() -> {
                errorMessageValidation.value = ("password must not be empty")
                isPasswordError.value = true
                return false
            }

            else -> return true
        }

    }


    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null) coroutine.launch {
            snackBarHostState.showSnackbar(errorMessage.value.toString())
            authKoin.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }) {

        it.calculateTopPadding()
        it.calculateBottomPadding()


        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 10.dp)
                .padding(
                    top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()
                )
                .fillMaxSize()
        ) {
            val (bottomRef, inputRef) = createRefs()
            Box(
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .constrainAs(bottomRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don’t have any account yet?",
                        color = CustomColor.neutralColor800,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16 / fontScall).sp
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clickable {
                                nav.navigate(Screens.Signup)
                            }) {
                        Text(
                            text = "Signup",
                            color = CustomColor.primaryColor700,
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16 / fontScall).sp

                        )

                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .constrainAs(inputRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalAlignment = Alignment.Start,
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
                    placeHolder = "Enter Your email",
                    errorMessage = errorMessageValidation.value,
                    isHasError = isEmailError.value,
                )
                TextSecureInputWithTitle(
                    password, "Password", isPasswordError.value, errorMessageValidation.value
                )
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        "Forget Password",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        color = CustomColor.neutralColor950,
                        fontSize = (16 / fontScall).sp,
                        modifier = Modifier.clickable {
                            nav.navigate(Screens.ReseatPasswordGraph)
                        })
                }

                Sizer(heigh = 30)


                CustomAuthBottom(
                    isLoading = isSendingData.value,
                    operation = {
                        keyboardController?.hide()
                        if (userNameOrEmail.value.text.isBlank() || password.value.text.isBlank()) {
                            coroutine.launch {
                                snackBarHostState.showSnackbar("User name Or Password is Blank")
                            }
                        } else {
                            coroutine.launch {
                                isSendingData.value = true

                                delay(10)

                                val token =  async { authKoin.generateTokenNotification() }.await()

//                                Pair(
//                                    "fv6pNFrXSsC7o29xq991br:APA91bHiUFcyvxKKxcqWoPZzoIaeWEs6_uN36YI0II5HHpN3HP-dUQap9UbnPiyBB8Fc5xX6GiCYbDQ7HxuBlXZkAE2P0T82-DRQ160EiKCJ9tlPgfgQxa4",
//                                    null
//                                )
                                if (!token.first.isNullOrEmpty()) {
                                    authKoin.loginUser(
                                        userNameOrEmail.value.text,
                                        password = password.value.text,
                                        nav = nav,
                                        token = token.first!!,
                                        isSendingData = isSendingData
                                    )

                                } else {
                                    isSendingData.value = false
                                    coroutine.launch {
                                        snackBarHostState.showSnackbar(
                                            token.second
                                                ?: "network must be connected to complete operation"
                                        )
                                    }
                                }
                            }


                        }
                    }, buttonTitle = "Login", validationFun = {
                        true
                        validateLoginInput(
                            username = userNameOrEmail.value.text, password = password.value.text
                        )

                    })

            }
        }


    }


}