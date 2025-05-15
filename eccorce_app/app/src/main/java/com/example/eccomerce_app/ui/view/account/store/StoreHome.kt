package com.example.eccomerce_app.ui.view.account.store

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
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
import com.example.eccomerce_app.R
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.Util.General.toCustomFil
import com.example.eccomerce_app.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.HomeViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    nav: NavHostController,
    homeViewModel: HomeViewModel
)
{

    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    val storeData = homeViewModel.myStore.collectAsState()
    val isLoading = homeViewModel.isLoading.collectAsState()

    val wall_paper_image= remember{ mutableStateOf<File?>(null) }
    val small_paper_image= remember{ mutableStateOf<File?>(null) }
    val storeName = remember { mutableStateOf(TextFieldValue("")) }
    val longint = remember { mutableStateOf(0.0) }
    val latit = remember {  mutableStateOf(0.0)}


    val isPigImage = remember { mutableStateOf(false) }


    val isPressAddNewAddress = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val currutine = rememberCoroutineScope()
    val isNotEnablePermission = remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = FocusRequester()

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val requestPermssion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permission ->
            val arePermissionsGranted = permission.values.reduce { acc, next ->
                acc && next
            }
            if (arePermissionsGranted) {
                currutine.launch(Dispatchers.Main) {

                    try {
                        val data = fusedLocationClient.lastLocation.await()
                        data?.let {
                                location->
                            longint.value = location.longitude?:5.5000
                            latit.value = location.latitude?:5.5000
                        }
                        if(longint.value==0.0)
                        {
                            longint.value=5.50000
                            latit.value=5.50000
                        }

                    } catch (e: SecurityException) {
                        var   error = "Permission exception: ${e.message}"
                    }

                }
            } else {
                isNotEnablePermission.value=true
            }
        }
    )

    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context);
            if (fileHolder != null){
                when(isPigImage.value)
                {
                    true->{
                        wall_paper_image.value = fileHolder
                    }
                    else->{
                       small_paper_image.value = fileHolder
                    }
                }

            }
        }
    }


    fun creationValidation(): Boolean{
        keyboardController?.hide()
        var errorMessage = "";
        if(wall_paper_image.value==null)
            errorMessage="You must select the wallpaper image"
        else if(small_paper_image.value==null)
            errorMessage="You must select the small image"
        else if(longint.value==0.0)
            errorMessage="You must select the store Location"
        else if(storeName.value.text.isEmpty())
            errorMessage="You write the store name"
        if(errorMessage.trim().isNotEmpty())
        {
            currutine.launch {
                snackbarHostState.showSnackbar(errorMessage)
            }
            return false
        }
        return true

    }



    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },

        bottomBar = {
            if(isPressAddNewAddress.value)
                ModalBottomSheet(
                    onDismissRequest = {
                        isPressAddNewAddress.value = false
                    },
                    sheetState = sheetState
                ) {

                    Column(modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()) {

                        Row(modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable{
                                    isPressAddNewAddress.value=false
                                    requestPermssion.launch(arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ))
                                }, horizontalArrangement = Arrangement.Start) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                "",
                                tint = CustomColor.neutralColor950,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Current Address",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = CustomColor.neutralColor950,
                            )
                        }

                        Sizer(20)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.outline_map),
                                "",
                                tint = CustomColor.neutralColor950,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Chose From Map",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = CustomColor.neutralColor950,
                            )
                        }


                    }
                }

        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(end = 15.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        "Store",
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
                actions = {
                    if(storeData.value==null){
                       TextButton(
                           enabled = isLoading.value==false,
                           onClick = {
                               if(creationValidation()){
                                   focusRequester.requestFocus()
                                   currutine.launch {
                                   var result = async{

                                       homeViewModel.createStore(
                                       name=storeName.value.text,
                                       wallpaper_image=wall_paper_image.value!!,
                                       small_image=small_paper_image.value!!,
                                       longitude=longint.value,
                                       latitude=latit.value,
                                           snackbarHostState
                                   );
                                   }.await()

                                   if(result!=null){
                                       snackbarHostState.showSnackbar(result)
                                   }else{
                                       wall_paper_image.value=null
                                       small_paper_image.value=null
                                       storeName.value= TextFieldValue("")
                                   }
                               }
                               }
                           }
                       ) {
                           when (isLoading.value){
                               true->{
                                   CircularProgressIndicator(
                                       modifier= Modifier.size(20.dp),
                                       strokeWidth = 2.dp
                                   )
                               }
                               else->{
                                   Text(
                                       "Create",
                                       fontFamily = General.satoshiFamily,
                                       fontWeight = FontWeight.Normal,
                                       fontSize = (16).sp,
                                       color = CustomColor.primaryColor700,
                                       textAlign = TextAlign.Center
                                   )
                               }
                           }
                       }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        Column(modifier=Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = it.calculateTopPadding()-29.dp)
            .padding(horizontal = 15.dp)
            .verticalScroll(rememberScrollState())
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){

            ConstraintLayout(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
            ) {

                val (bigImageRef,smalImageRef) = createRefs()

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                       // .padding(bottom = 15.dp)
                        .constrainAs(bigImageRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
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
                            .height(150.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = CustomColor.neutralColor500,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (wall_paper_image.value == null) {
                            true -> {
                                when (storeData.value?.pig_image.isNullOrEmpty()) {
                                    true -> {

                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                                            "",
                                            modifier = Modifier.size(80.dp),
                                            tint = CustomColor.neutralColor200
                                        )
                                    }

                                    else -> {

                                        SubcomposeAsyncImage(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                            ,
                                            model = General.handlingImageForCoil(
                                                storeData.value?.pig_image.toString(),
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
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp)),
                                    model = General.handlingImageForCoil(
                                        wall_paper_image.value!!.absolutePath,
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
                            .padding(end = 5.dp, bottom = 10.dp)
                            .constrainAs(cameralRef) {
                                end.linkTo(imageRef.end)
                                bottom.linkTo(imageRef.bottom)
                            }


                    ) {

                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                isPigImage.value = true;
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

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y=-50.dp)
                        .constrainAs(smalImageRef) {
                            top.linkTo(bigImageRef.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
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
                            )
                            .clip(RoundedCornerShape(60.dp))
                            .background(Color.White)
                        ,
                        contentAlignment = Alignment.Center
                    ) {
                        when (small_paper_image.value == null) {
                            true -> {
                                when (storeData.value?.small_image.isNullOrEmpty()) {
                                    true -> {

                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                                            "",
                                            modifier = Modifier.size(80.dp),
                                            tint = CustomColor.neutralColor200

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
                                                storeData.value?.small_image?:"",
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
                                        small_paper_image.value?.absolutePath,
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
                                keyboardController?.hide()
                                isPigImage.value = false;
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

            }

            Sizer(20)

            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "Store Location",
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (18).sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center,
                )
                IconButton(
                    onClick = {
                        keyboardController?.hide()

                        isPressAddNewAddress.value=true
                    }
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.location_address_list),
                            "",
                            modifier = Modifier.size(24.dp),
                        tint = CustomColor.primaryColor700
                    )
                }
            }
            Sizer(20)

            TextInputWithTitle(
                value = storeName,
                title = "Store Name",
                placHolder = storeData.value?.name ?: "Write Your Store Name",
                isHasError = false,
                focusRequester=focusRequester
            )

            if(isNotEnablePermission.value)
            {
                AlertDialog(
                    onDismissRequest = {
                        isNotEnablePermission.value=false
                    },
                    title = {
                        Text("Permission Required")
                    },
                    text = {
                        Text("You need to approve this permission in order to...")
                    },
                    confirmButton = {
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            //Logic when user denies to accept permissions
                        }) {
                            isNotEnablePermission.value=false;
                            Text("Deny")
                        }
                    })
            }




        }
    }



}