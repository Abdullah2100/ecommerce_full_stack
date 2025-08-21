package com.example.e_commercompose.View.component

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel

@Composable
fun CustomErrorSnackBar(
    authViewModel: AuthViewModel?=null,
//    homeViewModel: HomeViewModle?=null,
    page: @Composable() () -> Unit,
    nav: NavHostController?=null
){

//    val coroutine = rememberCoroutineScope()
//    val error = (if(homeViewModel==null) authViewModel!!.errorMessage
//            else homeViewModel.errorMessage).collectAsState()
//    val isVisible = remember { MutableTransitionState(false) }
//
//    val authData =if(homeViewModel!=null) homeViewModel
//        .authDataStreem
//        .collectAsStateWithLifecycle(AuthModleEntity(0,"","")) else null
//
////    if(homeViewModel!=null && authData?.value==null&&nav!=null){
////        nav.navigate(Screens.authGraph){
////            popUpTo(nav.graph.id){
////                inclusive=true
////            }
////        }
////    }
//
//    LaunchedEffect(authData?.value==null) {
//            if(homeViewModel!=null && authData?.value==null&&nav!=null){
//        nav.navigate(Screens.authGraph){
//            popUpTo(nav.graph.id){
//                inclusive=true
//            }
//        }
//    }
//    }
//
//    LaunchedEffect(error.value) {
//        if (error.value?.isNotEmpty() == true) {
//            isVisible.targetState = true
//        }
//    }
//
//    LaunchedEffect(error.value) {
//        if (error.value?.isNotEmpty() == true) {
//            coroutine.launch {
//                delay(5000)
//                isVisible.targetState = false
//                delay(50)
//               // viewModel.clearErrorMessage()
//                (when(homeViewModel==null){true->authViewModel!!.clearErrorMessage()
//                    else->homeViewModel.clearErrorMessage()})
//            }
//        }
//    }
//
//
//
//    val colors = listOf(
//        Color.Gray,
//        Color.Red,
//        Color.Green,
//        Color.White
//    )
//
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize()
//    ) { it ->
//        it.calculateTopPadding()
//        it.calculateBottomPadding()
//        page()
//
//        AnimatedVisibility(
//            isVisible,
//
//            modifier = Modifier
//
//        ) {
//            Box(
//                modifier = Modifier
//                    .padding(top = 40.dp)
//                    .height(50.dp)
//                    .fillMaxWidth()
//
//
//            ) {
//
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(horizontal = 15.dp)
//                        .background(
//                            color = colors[1],
//                            shape = RoundedCornerShape(7.dp)
//                        ),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Image(
//                        imageVector = Icons.Default.Warning,
//                        contentDescription = "",
//                        colorFilter = ColorFilter.tint(colors[3]),
//                        modifier = Modifier.padding(end = 5.dp)
//                    )
//
//                    Text(
//                        error.value.toString(),
//                        color = colors[3],
//                        fontSize = 20.sp,
//                        modifier = Modifier
//                            .fillMaxWidth(0.8f)
//                        // .background(Color.Red)
//                    )
//
//                }
//            }
//        }
//

//    }

}