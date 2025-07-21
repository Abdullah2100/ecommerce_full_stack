package com.example.e_commerc_delivery_man.ui

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.viewModel.AuthViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.e_commerc_delivery_man.ui.view.Auth.LoginScreen
import com.example.e_commerc_delivery_man.ui.view.Address.MapScreen
import com.example.e_commerc_delivery_man.ui.view.account.AccountPage
import com.example.e_commerc_delivery_man.ui.view.account.ProfileScreen
import com.example.e_commerc_delivery_man.ui.view.home.MyOrdersScreen
import com.example.e_commerc_delivery_man.ui.view.home.HomePage
import com.example.e_commerc_delivery_man.ui.view.home.OrdersScreen
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel


@Composable
fun NavController(
    nav: NavHostController,
    authViewModle: AuthViewModel = koinViewModel(),
    homeViewModle: HomeViewModel = koinViewModel(),
    currentScreen: Int,
) {

    Log.d("currentScreen", currentScreen.toString())
    NavHost(
        startDestination = when (currentScreen) {
            0 ->  Screens.Login
            else -> Screens.HomeGraph
        },
        navController = nav
    ) {





            composable<Screens.Login>(
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
            ) {
                LoginScreen(
                    nav = nav,
                    authViewModel = authViewModle
                )
            }



        navigation<Screens.HomeGraph>(
            startDestination = Screens.Home
        ) {

            composable<Screens.Home>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }
            ) {
                HomePage(
                    nav = nav,
                    homeViewModel = homeViewModle
                )
            }




            composable<Screens.Account>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }
            ) {
                AccountPage(
                    nav = nav,
                    homeViewModel = homeViewModle
                )
            }


            composable<Screens.Profile>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }
            ) {
                ProfileScreen(
                    nav = nav,
                    homeViewModel = homeViewModle
                )
            }


          composable<Screens.MyOrder>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }
            ) { navRef ->
                MyOrdersScreen(
                    nav = nav,
                    homeViewModel = homeViewModle,
                )

            }

           composable<Screens.Map>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }
            ) {
                MapScreen(
                    nav = nav,
                    homeViewModel = homeViewModle
                )

            }

            composable<Screens.Orders>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }
            ) {
                OrdersScreen(
                    nav = nav,
                    homeViewModel = homeViewModle
                )

            }



        }


    }

}