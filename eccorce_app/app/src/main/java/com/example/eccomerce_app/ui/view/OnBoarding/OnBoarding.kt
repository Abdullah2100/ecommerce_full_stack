package com.example.e_commercompose.ui.OnBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.Screens
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.viewModel.AuthViewModel


@Composable
fun OnBoardingScreen(
    nav: NavController,
    authViewModel: AuthViewModel
){
   val  fontScall= LocalDensity.current.fontScale
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()
        ConstraintLayout(modifier = Modifier
            .padding(
                top = it.calculateBottomPadding(),
                bottom = it.calculateBottomPadding()
            )
            .padding(horizontal = 15.dp)
            .fillMaxSize()) {
            val (titleRef,bottonReef) = createRefs();


            Column (
                modifier= Modifier
                    .padding(bottom = 50.dp)
                    .fillMaxWidth()
                    .constrainAs(titleRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.onboarding_log),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top=20.dp)
                )

                Text("Welcome to ShopZen",
                    fontWeight = FontWeight.Bold,
                    fontFamily = General.satoshiFamily,
                    color = CustomColor.neutralColor950,
                    fontSize = ((32 /fontScall ).sp)
                )

                Text(
                    "Your one-stop destination for hassle-free online shopping",
                    color = CustomColor.neutralColor800,
                    fontSize = (18/fontScall) .sp,
                    textAlign = TextAlign.Center,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top=5.dp)
                )
            }


            Button(
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .height(50.dp)
                    .fillMaxWidth()
                    .constrainAs(bottonReef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    },
                onClick = {
                    authViewModel.setIsPassOnBoardingScreen()
                  nav.navigate(Screens.AuthGraph){
                      popUpTo(nav.graph.id){
                          inclusive=true
                      }
                  }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColor.primaryColor700
                ),

            ) {

                Text("Get Started",
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (16/fontScall).sp
                    )
            }

        }
    }
}