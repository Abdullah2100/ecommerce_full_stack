package com.example.eccomerce_app.ui.component

import androidx.compose.foundation.Image
import com.example.eccomerce_app.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.ui.theme.CustomColor

@Composable
fun TextInputWithTitle(
    value: MutableState<TextFieldValue>,
    title: String,
    placHolder: String
){

    val  fontScall= LocalDensity.current.fontScale
    Column {
        Text(title,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Medium,
            color = CustomColor.neutralColor950,
            fontSize =(16/fontScall).sp)
        Sizer(heigh = 5)
        OutlinedTextField(

            maxLines = 1,
            value = value.value,
            onValueChange = { value.value = it },
            placeholder = {
                Text(
                    placHolder,
                    color = CustomColor.neutralColor500,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16/fontScall).sp
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.46f)
                , focusedBorderColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),

        )
    }

}


@Composable
fun TextSecureInputWithTitle(
    value: MutableState<TextFieldValue>,
){

    val showPassword = remember { mutableStateOf(false) }
    val  fontScall= LocalDensity.current.fontScale
    Column {
        Text("Password",
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Medium,
            color = CustomColor.neutralColor950,
            fontSize =(16/fontScall).sp)
Sizer(heigh = 5)

        OutlinedTextField(
            maxLines = 1,
            value = value.value,
            onValueChange = { value.value = it },
            placeholder = {
                Text(
                    "Enter your Password",
                    color = CustomColor.neutralColor500,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16/fontScall).sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()

            ,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.46f)
                , focusedBorderColor = Color.Black
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

            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon =
                {
                    val iconName = if (!showPassword.value) R.drawable.baseline_visibility_24
                    else R.drawable.visibility_off

                    IconButton(onClick = {
                        showPassword.value = !showPassword.value
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
    }

}