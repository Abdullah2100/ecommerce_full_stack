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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.example.e_commerc_delivery_man.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val fontScall = LocalDensity.current.fontScale
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutine = rememberCoroutineScope()


    val userNameOrEmail = remember { mutableStateOf(TextFieldValue("salime@gmail.com")) }
    val password = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }

    val isEmailError = remember { mutableStateOf<Boolean>(false) }
    val isPasswordError = remember { mutableStateOf<Boolean>(false) }
    val isSendingData = remember { mutableStateOf(false) }

    val errorMessage = remember { mutableStateOf("") }

    val snackBarHostState = remember { SnackbarHostState() }


//    val focusRequester = FocusRequester()

    fun validateLoginInput(
        username: String,
        password: String
    ): Boolean {

        isEmailError.value = false;
        isPasswordError.value = false;

        if (username.trim().isEmpty()) {
            errorMessage.value = "email must not be empty"
            isEmailError.value = true;
            return false;
        } else if (password.trim().isEmpty()) {
            errorMessage.value = ("password must not be empty")
            isPasswordError.value = true
            return false;
        }

        return true
    }



    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Login",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        color = CustomColor.neutralColor950,
                        fontSize = (34 / fontScall).sp
                    )
                }
            )
        }
    ) {

        it.calculateTopPadding()
        it.calculateBottomPadding()


        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .padding(top = it.calculateTopPadding() + 50.dp)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.Start,
        )
        {


            TextInputWithTitle(
                userNameOrEmail,
                title = "Email",
                placeHolder = "Enter Your email",
                isHasError = isEmailError.value,
                errorMessage = errorMessage.value,
            )

            TextSecureInputWithTitle(
                password,
                "Password",
                isPasswordError.value,
                errorMessage.value
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            )
            {
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
                            snackBarHostState.showSnackbar("User name Or Password is Blanck")
                        }
                    } else {
                        val token =// async { authKoin.generateTokenNotification() }.await()

                            Pair(
                                "fv6pNFrXSsC7o29xq991br:APA91bHiUFcyvxKKxcqWoPZzoIaeWEs6_uN36YI0II5HHpN3HP-dUQap9UbnPiyBB8Fc5xX6GiCYbDQ7HxuBlXZkAE2P0T82-DRQ160EiKCJ9tlPgfgQxa4",
                                null
                            )
                        if (!token.first.isNullOrEmpty())
                            coroutine.launch {
                                isSendingData.value = true;
                                val result = async {
                                    authViewModel.loginUser(
                                        userNameOrEmail.value.text,
                                        password = password.value.text,
                                        token = token.first
                                    )
                                }.await()
                                isSendingData.value = false
                                if (result == null) {
                                    userViewModel.getMyInfo()
                                    nav.navigate(Screens.LocationHome) {
                                        popUpTo(nav.graph.id) {
                                            inclusive = true
                                        }
                                    }
                                } else snackBarHostState.showSnackbar(result)
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


}