package com.example.eccomerce_app.ui.view.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.component.TextSecureInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.Screens
import com.example.hotel_mobile.Util.Validation
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(
    nav: NavHostController,
    authKoin: AuthViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val fontScall = LocalDensity.current.fontScale


    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()


    val errorMessage = authKoin.errorMessage.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    val name = remember { mutableStateOf(TextFieldValue("slime")) }
    val email = remember { mutableStateOf(TextFieldValue("salime@gmail.com")) }
    val phone = remember { mutableStateOf(TextFieldValue("778537385")) }
    val password = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }
    val confirmPassword = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }


    val isLoading = remember { mutableStateOf(false) }
    val isCheckBox = remember { mutableStateOf(false) }
    val isEmailError = remember { mutableStateOf(false) }
    val isNameError = remember { mutableStateOf(false) }
    val isPhoneError = remember { mutableStateOf(false) }
    val isPasswordError = remember { mutableStateOf(false) }
    val isPasswordConfirm = remember { mutableStateOf(false) }
    val isTermAndServicesError = remember { mutableStateOf(false) }
    val errorMessageValidation = remember { mutableStateOf("") }


    fun validateSignupInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        isNameError.value = false
        isEmailError.value = false
        isPasswordError.value = false
        isPasswordConfirm.value = false

        when {

            name.trim().isEmpty() -> {
                errorMessageValidation.value = "name must not be empty"
                isNameError.value = true
                return false
            }

            email.trim().isEmpty() -> {
                errorMessageValidation.value = "email must not be empty"
                isEmailError.value = true
                return false
            }

            !Validation.emailValidation(email) -> {
                errorMessageValidation.value = "write valid email"
                isEmailError.value = true
                return false
            }

            phone.value.text.trim().isEmpty() -> {
                errorMessageValidation.value = "phone must not Empty"
                isPhoneError.value = true
                return false
            }

            password.trim().isEmpty() -> {
                errorMessageValidation.value = ("password must not be empty")
                isPasswordError.value = true
                return false
            }

            !Validation.passwordSmallValidation(password) -> {
                errorMessageValidation.value = ("password must not contain two small letter")
                isPasswordError.value = true
                return false
            }

            !Validation.passwordNumberValidation(password) -> {
                errorMessageValidation.value = ("password must not contain two number")
                isPasswordError.value = true
                return false
            }

            !Validation.passwordCapitalValidation(password) -> {
                errorMessageValidation.value = ("password must not contain two capitalLetter")
                isPasswordError.value = true
                return false
            }

            !Validation.passwordSpicialCharracterValidation(password) -> {
                errorMessageValidation.value = ("password must not contain two spical character")
                isPasswordError.value = true
                return false
            }

            confirmPassword.trim().isEmpty() -> {
                errorMessageValidation.value = ("password must not be empty")
                isPasswordConfirm.value = true
                return false
            }

            password != confirmPassword -> {
                errorMessageValidation.value = ("confirm password not equal to password")
                isPasswordConfirm.value = true
                return false
            }

            !isCheckBox.value -> {
                errorMessageValidation.value = "Term And Policies is Required"
                isTermAndServicesError.value = true;

                return false
            }


            else -> return true
        }

    }

    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            coroutine.launch {
                snackBarHostState.showSnackbar(errorMessage.value.toString())
                authKoin.clearErrorMessage()
            }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(end = 15.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Signup",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Bold,
                            color = CustomColor.neutralColor950,
                            fontSize = (34 / fontScall).sp,
                            modifier = Modifier
                                .fillMaxWidth()

                        )
                    }

                },


                )
        },
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
            val (inputRef) = createRefs()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .constrainAs(inputRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start,
            ) {

                TextInputWithTitle(
                    name,
                    title = "Name",
                    placeHolder = "Enter Your name",
                    errorMessage = errorMessageValidation.value,
                    isHasError = isNameError.value,
                )

                TextInputWithTitle(
                    email,
                    title = "Email",
                    placeHolder = "Enter Your email",
                    errorMessage = errorMessageValidation.value,
                    isHasError = isEmailError.value,
                )
                TextInputWithTitle(
                    phone,
                    title = "Phone",
                    placeHolder = "Enter Phone",
                    errorMessage = errorMessageValidation.value,
                    isHasError = isPhoneError.value,
                )
                TextSecureInputWithTitle(
                    password,
                    "Password",
                    isPasswordError.value,
                    errorMessageValidation.value,
                )
                TextSecureInputWithTitle(
                    confirmPassword,
                    "Confirm Password",
                    isPasswordConfirm.value,
                    errorMessageValidation.value,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-10).dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Checkbox(
                        checked = isCheckBox.value,
                        onCheckedChange = { isCheckBox.value = !isCheckBox.value },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CustomColor.primaryColor700
                        ),
                        modifier = Modifier.padding()
                    )
                    Text(
                        "Agree With",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        color = if (isTermAndServicesError.value) CustomColor.alertColor_1_400 else CustomColor.neutralColor950,
                        fontSize = (16 / fontScall).sp,
                    )
                    Text(
                        "Term & Condition",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        color = if (isTermAndServicesError.value) CustomColor.alertColor_1_400 else CustomColor.primaryColor700,
                        fontSize = (16 / fontScall).sp,
                        modifier = Modifier
                            .padding(start = 3.dp)
                            .clickable {

                            },
                        textDecoration = TextDecoration.Underline
                    )
                }
                if (isTermAndServicesError.value)
                    Text(
                        errorMessageValidation.value,
                        color = CustomColor.alertColor_1_400,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (14 / fontScall).sp,
                        modifier = Modifier
                            .offset(x = (13).dp, y = (-12).dp)
                    )
//                Sizer(heigh = 10)

                CustomAuthBottom(
                    isLoading = isLoading.value,
                    validationFun = {
                        validateSignupInput(
                            email = email.value.text,
                            name = name.value.text,
                            password = password.value.text,
                            confirmPassword = confirmPassword.value.text
                        )
                    },
                    buttonTitle = "Signup",
                    operation = {
                        keyboardController?.hide()
                        coroutine.launch {
                            isLoading.value = true;
                            val token = async { authKoin.generateTokenNotification() }.await()
//                             Pair(
//                                "fv6pNFrXSsC7o29xq991br:APA91bHiUFcyvxKKxcqWoPZzoIaeWEs6_uN36YI0II5HHpN3HP-dUQap9UbnPiyBB8Fc5xX6GiCYbDQ7HxuBlXZkAE2P0T82-DRQ160EiKCJ9tlPgfgQxa4",
//                                null
//                            )

                            if (token.first != null) {
                                val result = authKoin.signUpUser(
                                    phone = phone.value.text,
                                    email = email.value.text,
                                    password = password.value.text,
                                    name = name.value.text,
                                    token = token.first!!,
                                    isLoading = isLoading
                                )
                                if (result.isNullOrEmpty())
                                    nav.navigate(Screens.LocationGraph) {
                                        popUpTo(nav.graph.id) {
                                            inclusive = true
                                        }
                                    }
                                else
                                    snackBarHostState.showSnackbar(result)
                            } else {
                                isLoading.value = false
                                coroutine.launch {
                                    snackBarHostState.showSnackbar(
                                        token.second
                                            ?: "network must be connected to complete operation"
                                    )

                                }
                            }
                        }
                    }
                )


            }
        }


    }


}