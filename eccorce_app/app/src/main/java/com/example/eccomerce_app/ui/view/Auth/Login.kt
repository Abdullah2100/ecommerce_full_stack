package com.example.e_commercompose.ui.view.Auth

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
import com.example.e_commercompose.viewModel.AuthViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
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
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.component.CustomAuthBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.component.TextSecureInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
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

    val isSendingData = authKoin.isLoading.collectAsState()
    val errorMessage = authKoin.errorMesssage.collectAsState()
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
                authKoin.clearErrorMessage()
            }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {

        it.calculateTopPadding()
        it.calculateBottomPadding()


        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 10.dp)
                .padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
                .fillMaxSize()
        ) {
            val (bottonRef, inputRef) = createRefs();
            Box(
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .constrainAs(bottonRef) {
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
                            }
                    ) {
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
//                        verticalArrangement = Arrangement.Center
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

                            authKoin.loginUser(
                                userNameOrEmail.value.text,
                                password = password.value.text,
                                nav = nav
                            )


                        }

                    },
                    buttonTitle = "Login",
                    validationFun = {
                        true
                        validateLoginInput(
                            username = userNameOrEmail.value.text,
                            password = password.value.text
                        )

                    }
                )

            }
        }


    }


//        }
//    )


}