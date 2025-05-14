package com.example.eccomerce_app.ui.view.account

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.eccomerce_app.R
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.Util.General.toCustomFil
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.ui.component.AccountCustomBottom
import com.example.eccomerce_app.ui.component.LogoutBotton
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.HomeViewModel
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val myInfo = homeViewModel.myInfo.collectAsState();

    val fullName = remember { mutableStateOf(TextFieldValue("")) }
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val phone = remember { mutableStateOf(TextFieldValue("")) }

    val context = LocalContext.current

    val file = remember { mutableStateOf<File?>(null) }
    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context);
            if (fileHolder != null)
                file.value = fileHolder;

        }
    }
    Scaffold(

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
                            Icons.Default.KeyboardArrowLeft,
                            "",
                            modifier = Modifier.size(30.dp),
                            tint = CustomColor.neutralColor950
                        )
                    }
                },
                scrollBehavior = scrollBehavior
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
                placHolder = myInfo.value?.name ?: "",
                isHasError = false,
                erroMessage = ""
            )

            TextInputWithTitle(
                value = email,
                title = "Email",
                placHolder = myInfo.value?.email ?: "",
                isHasError = false,
                erroMessage = "",
                isEnable = false
            )

            TextInputWithTitle(
                value = phone,
                title = "Phone",
                placHolder = myInfo.value?.phone ?: "",
                isHasError = false,
                erroMessage = "",
                isEnable = false
            )
        }
    }

}