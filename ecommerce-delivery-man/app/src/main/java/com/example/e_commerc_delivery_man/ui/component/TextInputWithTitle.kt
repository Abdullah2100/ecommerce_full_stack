package com.example.e_commerc_delivery_man.ui.component

import androidx.compose.foundation.Image
import com.example.e_commerc_delivery_man.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.ui.theme.CustomColor


@Composable
fun TextInputWithNoTitle(
    value: MutableState<TextFieldValue>,
    placHolder: String,
) {

    val fontScall = LocalDensity.current.fontScale
    Column {
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
                    fontSize = (16 / fontScall).sp
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CustomColor.neutralColor50,
                focusedBorderColor = Color.Black
            ),

            textStyle = TextStyle(
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor950
            ),

            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),

            )
    }

}


@Composable
fun TextInputWithTitle(
    value: MutableState<TextFieldValue>,
    title: String,
    placeHolder: String,
    isHasError: Boolean = false,
    errorMessage: String? = null,
    isEnable: Boolean? = true,
) {

    val modifierWithFocus =
        Modifier.fillMaxWidth()
    val fontScall = LocalDensity.current.fontScale
    Column {
        if (title.trim().isNotEmpty())
            Text(
                title,
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                color = CustomColor.neutralColor950,
                fontSize = (16 / fontScall).sp
            )
        Sizer(heigh = 5)
        OutlinedTextField(
            enabled = isEnable == true,
            maxLines = 1,
            value = value.value,
            onValueChange = { value.value = it },
            placeholder = {
                Text(
                    placeHolder,
                    color = CustomColor.neutralColor500,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16 / fontScall).sp
                )
            },
            modifier = modifierWithFocus,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (!isHasError) Color.Gray.copy(alpha = 0.46f) else CustomColor.alertColor_1_400,
                focusedBorderColor = if (!isHasError) Color.Black else CustomColor.alertColor_1_400
            ),
            supportingText = {
                if (isHasError && errorMessage != null)
                    Text(
                        errorMessage,
                        color = CustomColor.alertColor_1_400,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (14 / fontScall).sp,
                        modifier = Modifier.offset(x = -15.dp)
                    )
            },
            textStyle = TextStyle(
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor950
            ),
            trailingIcon = {
                if (isHasError) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.allert),
                        contentDescription = "",
                        tint = CustomColor.alertColor_1_400
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),

            )
    }

}

@Composable
fun TextNumberInputWithTitle(
    value: MutableState<TextFieldValue>,
    title: String,
    placHolder: String,
    isHasError: Boolean = false,
    erroMessage: String
) {

    val pattern = remember { Regex("^\\d+\$") }
    val fontScall = LocalDensity.current.fontScale
    Column {
        Text(
            title,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Medium,
            color = CustomColor.neutralColor950,
            fontSize = (16 / fontScall).sp
        )
        Sizer(heigh = 5)
        OutlinedTextField(

            maxLines = 1,
            value = value.value,
            onValueChange = {
                if ((it.text.isEmpty() || it.text.matches(pattern)) && it.text.length < 13)
                    value.value = it
            },
            placeholder = {
                Text(
                    placHolder,
                    color = CustomColor.neutralColor500,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16 / fontScall).sp
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (!isHasError) Color.Gray.copy(alpha = 0.46f) else CustomColor.alertColor_1_400,
                focusedBorderColor = if (!isHasError) Color.Black else CustomColor.alertColor_1_400
            ),
            supportingText = {
                if (isHasError)
                    Text(
                        erroMessage,
                        color = CustomColor.alertColor_1_400,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (14 / fontScall).sp,
                        modifier = Modifier.offset(x = -15.dp)
                    )
            },
            textStyle = TextStyle(
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor950
            ),
            trailingIcon = {
                if (isHasError) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.allert),
                        contentDescription = "",
                        tint = CustomColor.alertColor_1_400
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),

            )
    }

}


@Composable
fun TextSecureInputWithTitle(
    value: MutableState<TextFieldValue>,
    title: String = "",
    isHasError: Boolean = false,
    errMessage: String
) {

    val showPassword = remember { mutableStateOf(false) }
    val fontScall = LocalDensity.current.fontScale
    Column {
        Text(
            title,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Medium,
            color = CustomColor.neutralColor950,
            fontSize = (16 / fontScall).sp
        )
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
                    fontSize = (16 / fontScall).sp
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (!isHasError) Color.Gray.copy(alpha = 0.46f) else CustomColor.alertColor_1_400,
                focusedBorderColor = if (!isHasError) Color.Black else CustomColor.alertColor_1_400
            ),
            supportingText = {
                if (isHasError)
                    Text(
                        errMessage,
                        color = CustomColor.alertColor_1_400,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (14 / fontScall).sp,
                        modifier = Modifier.offset(x = -15.dp)
                    )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {}),
            textStyle = TextStyle(
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor950
            ),
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
                                color = if (isHasError) CustomColor.alertColor_1_400 else Color.Gray.copy(
                                    alpha = 0.46f
                                )
                            )
                        )
                    }
                }

        )
    }

}