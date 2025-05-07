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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.component.TextSecureInputWithTitle
import com.example.eccomerce_app.ui.theme.CustomColor

@Composable
fun LoginScreen(
    nav: NavHostController,
    authKoin: AuthViewModel
) {
    val isLoading = authKoin.isLoadin.collectAsState()
    val fontScall = LocalDensity.current.fontScale

    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    val userNameOrEmail = remember { mutableStateOf(TextFieldValue("fackkk@gmail.com")) }
    val password = remember { mutableStateOf(TextFieldValue("AS!@as23")) }



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
                                //  nav.navigate(Screens.Signup)
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
                TextInputWithTitle(userNameOrEmail, placHolder = "Enter Your email/username", title = "Email")
                Sizer(heigh = 15)
                TextSecureInputWithTitle(password)
                Sizer(heigh = 10)
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
                        modifier = Modifier.clickable{

                        }
                    )
                }

                Sizer(heigh = 30)


                Button(
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                    onClick = {
                        authKoin.loginUser(userNameOrEmail.value.text
                            ,password=password.value.text,
                            snackbarHostState)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomColor.primaryColor700
                    ),

                    ) {

                    when(isLoading.value){
                         true->{
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(30.dp)
                            )

                        }
                        else ->{

                    Text(
                        "Login",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (16 / fontScall).sp
                    )
                        }
                    }
                }


            }
        }


    }


//        }
//    )


}