package com.example.eccomerce_app.View.Pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.eccomerce_app.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(
    nav: NavHostController,
    authKoin: AuthViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current


//    val loadingStatus = finalScreenViewModel.statusChange.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val datePickerState = rememberDatePickerState()
    val showDatePicker = remember { mutableStateOf(false) }
    val selectedDateInMillis = datePickerState.selectedDateMillis

    val isShownPassword = remember { mutableStateOf(false) }

    val email = remember { mutableStateOf(TextFieldValue("fackkk@gmail.com")) }
    val userName = remember { mutableStateOf(TextFieldValue("ffffff")) }
    val name = remember { mutableStateOf(TextFieldValue("mosaaa")) }
    val address = remember { mutableStateOf(TextFieldValue("adsfasdf")) }
    val phone = remember { mutableStateOf(TextFieldValue("778537385")) }
    val password = remember { mutableStateOf(TextFieldValue("as!@AS23")) }

    val brithDay = remember(selectedDateInMillis) {
        selectedDateInMillis?.let {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date(it))
        } ?: "لم يتم اختيار اي تاريخ"
    }




//    CustomErrorSnackBar(
////        authViewModel = finalScreenViewModel,
//        page = {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                }
            ) {
                it.calculateTopPadding()
                it.calculateBottomPadding()


                ConstraintLayout(
                    modifier = Modifier.clickable {
                        keyboardController?.hide()
                    }
                ) {
                    val (goToReiginster, form) = createRefs();


                    if (showDatePicker.value)
                      {
                                DatePickerDialog(
                                    onDismissRequest = {
                                        showDatePicker.value = false

                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                showDatePicker.value = false
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Transparent
                                            )
                                        ) {

                                            Text("تم", color = Color.Black)
                                        }
                                    },
                                    dismissButton = {
                                        Button(
                                            onClick = {
                                                showDatePicker.value = false
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Transparent
                                            )
                                        ) {
                                            Text("الغاء", color = Color.Black)
                                        }
                                    },
                                )
                                {
                                    DatePicker(state = datePickerState)

                            }
                    }


                    Column(
                        modifier = Modifier
                            .constrainAs(form) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .fillMaxHeight(0.9f)
                            .fillMaxWidth()
                            .padding(top = 50.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {



                        OutlinedTextField(
                            maxLines = 1,
                            value = name.value,
                            onValueChange = { name.value = it },
                            placeholder = {
                                Text(
                                    "الاسم",
                                    color = Color.Gray.copy(alpha = 0.66f)
                                )
                            },
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(19.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.46f)
                                , unfocusedTextColor = Color.Gray.copy(alpha = 0.46f)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        )


                        OutlinedTextField(
                            maxLines = 1,
                            value = userName.value,
                            onValueChange = { userName.value = it },
                            placeholder = {
                                Text(
                                    "اسم المستخدم",
                                    color = Color.Gray.copy(alpha = 0.66f)
                                )
                            },
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(19.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.46f)

                                , unfocusedTextColor = Color.Gray.copy(alpha = 0.46f)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        )

                        OutlinedTextField(
                            maxLines = 1,
                            value = email.value,
                            onValueChange = { email.value = it },
                            placeholder = {
                                Text(
                                    "الايميل",
                                    color = Color.Gray.copy(alpha = 0.66f)
                                )
                            },
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(19.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.46f),

                                 unfocusedTextColor = Color.Gray.copy(alpha = 0.46f)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onDone = {

                            })
                        )

                        OutlinedTextField(
                            maxLines = 1,
                            value = phone.value,
                            onValueChange = { phone.value = it },
                            placeholder = {
                                Text(
                                    "رقم الهاتف",
                                    color = Color.Gray.copy(alpha = 0.66f)
                                )
                            },
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(19.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.46f),

                                unfocusedTextColor = Color.Gray.copy(alpha = 0.46f)
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                showDatePicker.value = true

                            }),


                            )

                        Button(

                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    color = Color.Gray.copy(alpha = 0.46f),
                                    shape = RoundedCornerShape(19.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ), onClick = {
                                showDatePicker.value = true
                            }) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column {
                                    Text(
                                        text = "تاريخ الميلاد",
                                        color = Color.Gray.copy(alpha = 0.66f)
                                    )
                                    if (brithDay.length > 0)
                                        Text(
                                            text = brithDay,
                                            color = Color.Gray.copy(alpha = 0.66f)
                                        )
                                }
                                Image(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(
                                        color = Color.Gray.copy(alpha = 0.66f)
                                    )
                                )
                            }

                        }
                        OutlinedTextField(
                            maxLines = 1,
                            value = address.value,
                            onValueChange = { address.value = it },
                            placeholder = {
                                Text(
                                    "العنوان",
                                    color = Color.Gray.copy(alpha = 0.66f)
                                )
                            },
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(19.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.46f),

                                unfocusedTextColor = Color.Gray.copy(alpha = 0.46f)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onDone = {

                            })
                        )



                        OutlinedTextField(
                            maxLines = 1,
                            value = password.value,
                            onValueChange = { password.value = it },
                            placeholder = {
                                Text(
                                    "كملة المرور",
                                    color = Color.Gray.copy(alpha = 0.66f)
                                )
                            },
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth()

                            ,
                            shape = RoundedCornerShape(19.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.46f),

                                unfocusedTextColor = Color.Gray.copy(alpha = 0.46f)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                               /* finalScreenViewModel.signUpUser(
                                    SingUpDto(
                                        name = name.value.toString(),
                                        email = email.value.toString(),
                                        phone = phone.value.toString(),
                                        address = address.value.toString(),
                                        password = password.value.toString(),
                                        isVip = false,
                                        brithDay = General.convertMilisecondToLocalDateTime(selectedDateInMillis) ,
                                        imagePath = null,
                                        userName = userName.value.toString()
                                    ),
                                    snackbarHostState =snackbarHostState,
                                    navController = nav


                                )*/
                            }),

                            visualTransformation = if (isShownPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon =
                            {
                                val iconName = if (!isShownPassword.value) R.drawable.baseline_visibility_24
                                else R.drawable.visibility_off

                                IconButton(onClick = {
                                    isShownPassword.value = !isShownPassword.value
                                }) {
                                    Image(
                                        painterResource(iconName), contentDescription = "",
                                        colorFilter = ColorFilter.tint(
                                            color = Color.Gray.copy(alpha = 0.46f)
                                        )
                                    )
                                }
                            }

                        )

                        Button(
                            //enabled = loadingStatus.value != enNetworkStatus.Loading,
                            onClick = {
                                keyboardController?.hide();
                                /*finalScreenViewModel.signUpUser(
                                    SingUpDto(
                                        name = name.value.text,
                                        email = email.value.text,
                                        phone = phone.value.text,
                                        address = address.value.text,
                                        password = password.value.text,
                                        isVip = false,
                                        brithDay = General.convertMilisecondToLocalDateTime(selectedDateInMillis),
                                        imagePath = null,
                                        userName = userName.value.text
                                    ),
                                    snackbarHostState =snackbarHostState
                                    , navController = nav


                                )*/
                            },
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth()
                                .height(35.dp)
                        ) {
                            /*when (loadingStatus.value) {
                                enNetworkStatus.Loading -> {
                                    CircularProgressIndicator(
                                        color = Color.Blue, modifier = Modifier
                                            .offset(y = -3.dp)
                                            .height(25.dp)
                                            .width(25.dp)
                                    )
                                }

                                else -> {
                                    Text(
                                        "تسجيل",
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }
                            }
*/
                        }
                    }

                    Box(modifier = Modifier
                        .constrainAs(goToReiginster) {
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                            start.linkTo(parent.start)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "لديك  حساب")
                            Box(
                                modifier = Modifier
                                    .zIndex(20f)
                                    .padding(start = 5.dp)
                                    .clickable {
//                                        nav.navigate(Screens.login) {
//                                            popUpTo(Screens.login) { inclusive = true }
//                                        }
nav.popBackStack()

                                    }
                            ) {
                                Text(text = "دخول", color = Color.Blue)

                            }
                        }
                    }

                }


            }
//        }
//    )

}