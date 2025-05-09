package com.example.eccomerce_app.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.eccomerce_app.ui.OnBoarding.OnBoardingScreen
import com.example.eccomerce_app.ui.view.Auth.LoginScreen
import com.example.eccomerce_app.View.Pages.SignUpPage
import com.example.eccomerce_app.ui.view.home.HomePage


@Composable
fun NavController(
    nav: NavHostController,
    isPassOnBoardingScreen: Boolean,
    isLoginIn: Boolean,
    authViewModle: AuthViewModel = koinViewModel(),
) {

    NavHost(
        startDestination =if(isLoginIn) Screens.HomeGraph else if(isPassOnBoardingScreen) Screens.AuthGraph else Screens.OnBoarding,
        navController = nav
    ){

        composable < Screens.OnBoarding>(
           enterTransition = {
               return@composable fadeIn(tween(2000))
           },
            exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
                )
            },

        ){
            OnBoardingScreen(nav,authViewModle)
        }
        navigation < Screens.AuthGraph>(
            startDestination = Screens.Login
        ){

            composable < Screens.Login>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                },

               popEnterTransition = {
                   return@composable slideIntoContainer(
                       AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                   )
               },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }
            ){
                LoginScreen(
                    nav = nav,
                    authKoin =authViewModle
                )
            }
            composable < Screens.Signup>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },

                popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                },
            ){
                SignUpPage(
                    nav = nav,
                    authKoin =authViewModle
                )
            }
        }

        navigation < Screens.HomeGraph>(
            startDestination = Screens.Home
        ){

            composable < Screens.Home>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                },

                popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }
            ){
                HomePage(
//                    nav = nav,
//                    authKoin =authViewModle
                )
            }

        }


    }

}