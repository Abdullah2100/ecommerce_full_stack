package com.example.eccomerce_app.ui.view.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.component.CustomAuthBotton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.component.TextNumberInputWithTitle
import com.example.e_commercompose.ui.component.TextSecureInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.hotel_mobile.Util.Validation
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(
    nav: NavHostController,
    authKoin: AuthViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val fontScall = LocalDensity.current.fontScale

    val focusRequester = FocusRequester()

    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()


    val errorMessage = authKoin.errorMessage.collectAsState()
    val isLoading = authKoin.isLoading.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    val name = remember { mutableStateOf(TextFieldValue("slime")) }
    val email = remember { mutableStateOf(TextFieldValue("salime@gmail.com")) }
    val phone = remember { mutableStateOf(TextFieldValue("778537385")) }
    val password = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }
    val confirmPassword = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }

    val isCheckBox = remember { mutableStateOf(false) }
    val isEmailError = remember { mutableStateOf(false) }
    val isNameError = remember { mutableStateOf(false) }
    val isPhoneError = remember { mutableStateOf(false) }
    val isPasswordError = remember { mutableStateOf(false) }
    val isPasswordConfirm = remember { mutableStateOf(false) }
    val errorMessageValidation = remember { mutableStateOf("") }


    fun validateLoginInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        isNameError.value = false
        isEmailError.value = false
        isPasswordError.value = false
        isPasswordConfirm.value = false

        if (name.trim().isEmpty()) {
            errorMessageValidation.value = "name must not be empty"
            isNameError.value = true
            return false
        }

        if (email.trim().isEmpty()) {
            errorMessageValidation.value = "email must not be empty"
            isEmailError.value = true
            return false
        } else if (!Validation.emailValidation(email)) {
            errorMessageValidation.value = "write valid email"
            isEmailError.value = true
            return false
        } else if (phone.value.text.trim().isEmpty()) {
            errorMessageValidation.value = "phone must not Empty"
            isPhoneError.value = true
            return false
        } else if (password.trim().isEmpty()) {
            errorMessageValidation.value = ("password must not be empty")
            isPasswordError.value = true
            return false
        } else if (!Validation.passwordSmallValidation(password)) {
            errorMessageValidation.value = ("password must not contain two small letter")
            isPasswordError.value = true
            return false
        } else if (!Validation.passwordNumberValidation(password)) {
            errorMessageValidation.value = ("password must not contain two number")
            isPasswordError.value = true
            return false
        } else if (!Validation.passwordCapitalValidation(password)) {
            errorMessageValidation.value = ("password must not contain two capitalLetter")
            isPasswordError.value = true
            return false
        } else if (!Validation.passwordSpicialCharracterValidation(password)) {
            errorMessageValidation.value = ("password must not contain two spical character")
            isPasswordError.value = true
            return false
        } else if (confirmPassword.trim().isEmpty()) {
            errorMessageValidation.value = ("password must not be empty")
            isPasswordConfirm.value = true
            return false
        } else if (password != confirmPassword) {
            errorMessageValidation.value = ("confirm password not equal to password")
            isPasswordConfirm.value = true
            return false
        } else if (!isCheckBox.value) {

            coroutine.launch { snackBarHostState.showSnackbar("You should check the condition box to signup") }

            return false
        }


        return true
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
//                        verticalArrangement = Arrangement.Center
            ) {

                TextInputWithTitle(
                    name,
                    title = "Name",
                    placHolder = "Enter Your name",
                    isHasError = isNameError.value,
                    erroMessage = errorMessageValidation.value,
                    focusRequester = focusRequester
                )
                TextInputWithTitle(
                    email,
                    title = "Email",
                    placHolder = "Enter Your email",
                    isHasError = isEmailError.value,
                    erroMessage = errorMessageValidation.value,
                    focusRequester = focusRequester
                )
                TextNumberInputWithTitle(
                    phone,
                    placHolder = "Enter Phone",
                    title = "Phone",
                    isHasError = isPhoneError.value,
                    erroMessage = errorMessageValidation.value
                )
                TextSecureInputWithTitle(
                    password,
                    "Password",
                    isPasswordError.value,
                    errorMessageValidation.value
                )
                TextSecureInputWithTitle(
                    confirmPassword,
                    "Confirm Password",
                    isPasswordConfirm.value,
                    errorMessageValidation.value
                )
                Box(
                    modifier = Modifier.background(Color.Red)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(), horizontalArrangement = Arrangement.Start,
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
                            color = CustomColor.neutralColor950,
                            fontSize = (16 / fontScall).sp,
                        )
                        Text(
                            "Term & Condition",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Medium,
                            color = CustomColor.primaryColor700,
                            fontSize = (16 / fontScall).sp,
                            modifier = Modifier
                                .padding(start = 3.dp)
                                .clickable {

                                },
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

//                Sizer(heigh = 10)

                CustomAuthBotton(
                    isLoading = isLoading.value,
                    validationFun = {
                        validateLoginInput(
                            email = email.value.text,
                            name = name.value.text,
                            password = password.value.text,
                            confirmPassword = confirmPassword.value.text
                        )
                    },
                    buttonTitle = "Login",
                    operation = {
                        keyboardController?.hide()

                        authKoin.signUpUser(
                            phone = phone.value.text,
                            email = email.value.text,
                            password = password.value.text,
                            name = name.value.text,
                            nav = nav
                        )
                    })


            }
        }


    }


}