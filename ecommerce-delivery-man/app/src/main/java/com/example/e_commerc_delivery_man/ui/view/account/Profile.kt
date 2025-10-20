package com.example.e_commerc_delivery_man.ui.view.account

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.util.General
import com.example.e_commerc_delivery_man.util.General.toCustomFil
import com.example.e_commerc_delivery_man.ui.component.TextInputWithTitle
import com.example.e_commerc_delivery_man.ui.component.TextNumberInputWithTitle
import com.example.e_commerc_delivery_man.ui.component.TextSecureInputWithTitle
import com.example.e_commerc_delivery_man.ui.theme.CustomColor
import com.example.e_commerc_delivery_man.viewModel.UserViewModel
import com.example.hotel_mobile.Util.Validation
import kotlinx.coroutines.launch
import java.io.File


@SuppressLint("RememberInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val myInfo = userViewModel.myInfo.collectAsState();

    val snackbarHostState = remember { SnackbarHostState() }

    val fullName = remember { mutableStateOf(TextFieldValue("")) }
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val phone = remember { mutableStateOf(TextFieldValue("")) }
    val oldPassword = remember { mutableStateOf(TextFieldValue("")) }
    val newPassword = remember { mutableStateOf(TextFieldValue("")) }

    val currotine = rememberCoroutineScope()

    val file = remember { mutableStateOf<File?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Log.d("thumnail",myInfo.value?.thumbnail.toString())


    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context);
            if (fileHolder != null){
                file.value = fileHolder;
            }
        }
    }

    fun validateLoginInput(

    ): Boolean {
        var erroMessage=""

        if(oldPassword.value.text.isEmpty()&&newPassword.value.text.isEmpty()&&phone.value.text.isEmpty())
            return true;
        else if (phone.value.text.trim().length<9) {
            erroMessage = "Write Valide Phone"
        }
        else  if (oldPassword.value.text.isNotEmpty()&&!Validation.passwordSmallValidation(oldPassword.value.text)) {
            erroMessage = ("password must not contain two small letter")
        } else if (oldPassword.value.text.isNotEmpty()&&!Validation.passwordNumberValidation(oldPassword.value.text)) {
            erroMessage = ("password must not contain two number")
        } else if (oldPassword.value.text.isNotEmpty()&&!Validation.passwordCapitalValidation(oldPassword.value.text)) {
            erroMessage = ("password must not contain two capitalLetter")
        } else if (oldPassword.value.text.isNotEmpty()&&!Validation.passwordSpicialCharracterValidation(oldPassword.value.text)) {
            erroMessage = ("password must not contain two spical character")
        } else if (newPassword.value.text.isNotEmpty()&&newPassword.value.text.trim().isEmpty()) {
            erroMessage = ("password must not be empty")
        } else if (oldPassword.value.text.isNotEmpty()&&newPassword.value.text.isNotEmpty()&&oldPassword.value.text != newPassword.value.text) {
            erroMessage = ("confirm password not equal to password")
        }

        if (erroMessage.isNotEmpty()) {
            currotine.launch {

                snackbarHostState.showSnackbar(erroMessage)
            }
            return false;
        }


        return erroMessage.isEmpty();
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },

        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        "My Profile",
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (24).sp,
                        color = CustomColor.neutralColor950,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            nav.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            "",
                            modifier = Modifier.size(30.dp),
                            tint = CustomColor.neutralColor950
                        )
                    }
                },

                actions = {
//                    if(
//                        file.value!=null||
//                        (fullName.value.text.isNotEmpty()&&
//                                fullName.value.text!=myInfo.value?.name)||
//                        newPassword.value.text.isNotEmpty()||
//                        oldPassword.value.text.isNotEmpty()||
//                        phone.value.text.isNotEmpty()
//
//                        )
//
//                    TextButton(
//                        onClick = {
//                            keyboardController?.hide()
//                            var result = validateLoginInput()
//                            if((result)==true){
//                                var data = MyInfoUpdate(
//                                    name =if(fullName.value.text.isEmpty())null else fullName.value.text,
//                                    oldPassword =if(oldPassword.value.text.isEmpty())null else oldPassword.value.text,
//                                    newPassword =if(newPassword.value.text.isEmpty())null else newPassword.value.text,
//                                    phone =if(phone.value.text.isEmpty())null else phone.value.text,
//                                    thumbnail = file.value,
//                                )
//                                currotine.launch {
////                                    var result=async {
////                                        homeViewModel.updateMyInfo(data);
////                                    }.await()
////                                    if(result.isNullOrEmpty()){
////                                        phone.value= TextFieldValue("")
////                                        oldPassword.value= TextFieldValue("")
////                                        newPassword.value= TextFieldValue("")
////                                        fullName.value= TextFieldValue("")
////                                        file===null
////                                        currotine.launch {
////                                            snackbarHostState.showSnackbar("profile update seccessfuly");
////                                        }
////                                    }else{
////                                        currotine.launch {
////                                            snackbarHostState.showSnackbar(result);
////                                        }
////                                    }
//                                }
//                            }
//                        }
//                    ) {
//                        Text(
//                            "Save",
//                            fontFamily = General.satoshiFamily,
//                            fontWeight = FontWeight.Normal,
//                            fontSize = (18).sp,
//                            color = CustomColor.primaryColor700,
//                            textAlign = TextAlign.Center
//                        )
//                    }
                },
//                scrollBehavior = scrollBehavior
            )
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = it.calculateTopPadding() + 20.dp)
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            ) {
                val (imageRef, cameralRef) = createRefs()
                Box(
                    modifier = Modifier
                        .constrainAs(imageRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .height(110.dp)
                        .width(110.dp)
                        .border(
                            width = 1.dp,
                            color = CustomColor.neutralColor500,
                            shape = RoundedCornerShape(60.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (file.value == null) {
                        true -> {
                            when (myInfo.value?.thumbnail.isNullOrEmpty()) {
                                true -> {

                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.user),
                                        "",
                                        modifier = Modifier.size(80.dp)
                                    )
                                }

                                else -> {

                                    SubcomposeAsyncImage(
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
//                                                .padding(top = 35.dp)
                                            .height(90.dp)
                                            .width(90.dp)
                                            .clip(RoundedCornerShape(50.dp)),
                                        model = General.handlingImageForCoil(
                                            myInfo.value!!.thumbnail,
                                            context
                                        ),
                                        contentDescription = "",
                                        loading = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize(),
                                                contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                            ) {
                                                CircularProgressIndicator(
                                                    color = Color.Black,
                                                    modifier = Modifier.size(54.dp) // Adjust the size here
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                        }

                        else -> {
                            SubcomposeAsyncImage(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
//                                                .padding(top = 35.dp)
                                    .height(90.dp)
                                    .width(90.dp)
                                    .clip(RoundedCornerShape(50.dp)),
                                model = General.handlingImageForCoil(
                                    file.value!!.absolutePath,
                                    context
                                ),
                                contentDescription = "",
                                loading = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color.Black,
                                            modifier = Modifier.size(54.dp) // Adjust the size here
                                        )
                                    }
                                },
                            )
                        }
                    }

                }
                Box(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .constrainAs(cameralRef) {
                            end.linkTo(imageRef.end)
                            bottom.linkTo(imageRef.bottom)
                        }


                ) {

                    IconButton(
                        onClick = {
                            onImageSelection.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .size(30.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = CustomColor.primaryColor200
                        )
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.camera),
                            "",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }

            }



            TextInputWithTitle(
                value = fullName,
                title = "Full Name",
                placeHolder = myInfo.value?.user?.name ?: "",
                errorMessage = "",
            )

            TextInputWithTitle(
                value = email,
                title = "Email",
                placeHolder = myInfo.value?.user?.email ?: "",
                errorMessage = "",
                isEnable = false,
            )

            TextNumberInputWithTitle(
                value = phone,
                title = "Phone",
                placHolder = myInfo.value?.user?.phone ?: "",
                isHasError = false,
                erroMessage = "",
            )
            TextSecureInputWithTitle(
                value = oldPassword,
                title = "Current Password",
                isHasError = false,
                errMessage = ""
            )

            TextSecureInputWithTitle(
                value = newPassword,
                title = "New Password",
                isHasError = false,
                errMessage = ""
            )
        }
    }

}