package com.example.e_commerc_delivery_man.ui

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.viewModel.AuthViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.e_commerc_delivery_man.ui.view.Address.MapHomeScreen
import com.example.e_commerc_delivery_man.ui.view.Auth.LoginScreen
import com.example.e_commerc_delivery_man.ui.view.account.AccountPage
import com.example.e_commerc_delivery_man.ui.view.account.ProfileScreen
import com.example.e_commerc_delivery_man.ui.view.home.MyOrdersScreen
import com.example.e_commerc_delivery_man.ui.view.home.HomePage
import com.example.e_commerc_delivery_man.ui.view.home.OrdersScreen
import com.example.e_commerc_delivery_man.viewModel.OrderViewModel
import com.example.e_commerc_delivery_man.viewModel.UserViewModel
import com.example.eccomerce_app.ui.view.address.AddressHomeScreen
import com.example.eccomerce_app.viewModel.MapViewModel


@Composable
fun NavController(
    nav: NavHostController,
    authViewModel: AuthViewModel = koinViewModel(),
    orderViewModel: OrderViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    mapViewModel: MapViewModel = koinViewModel(),
    currentScreen: Int,
) {

    Log.d("currentScreen", currentScreen.toString())
    NavHost(
        startDestination = when (currentScreen) {
            0 -> Screens.Login
            1 -> Screens.LocationHome
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
                authViewModel = authViewModel,
                userViewModel = userViewModel
            )
        }



        composable<Screens.Map>(
            enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(250)
                )
            },
            exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(250)
                )
            }
        ) { result ->
            val data = result.toRoute<Screens.Map>()

            MapHomeScreen(
                nav = nav,
                userViewModel = userViewModel,
                longitude = data.lognit,
                latitude = data.latitt,
                additionLat = data.additionLat,
                additionLong = data.additionLong,
                title = data.title,
                id = data.id,
                mapType = data.mapType,
                isFomLogin = data.isFromLogin,
                mapViewModel = mapViewModel,
                orderViewModel = orderViewModel
            )

        }

        composable<Screens.LocationHome>(
            enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            },

            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }) {
            AddressHomeScreen(
                nav = nav,
                userViewModel = userViewModel
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
                    userViewModel = userViewModel
                )
            }




            composable<Screens.Account>(
                enterTransition = { return@composable fadeIn(tween(200)) },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }
            ) {
                AccountPage(
                    nav = nav,
                    userModelView = userViewModel,
                    authViewModel = authViewModel
                )
            }


            composable<Screens.Profile>(
                enterTransition = { return@composable fadeIn(tween(200)) },
                exitTransition = { return@composable fadeOut(tween(200)) }
            ) {
                ProfileScreen(
                    nav = nav,
                    userViewModel = userViewModel
                )
            }


            composable<Screens.MyOrder>(

                enterTransition = { return@composable fadeIn(tween(200)) },
                exitTransition = { return@composable fadeOut(tween(200)) }
            ) { navRef ->
                MyOrdersScreen(
                    nav = nav,
                    orderViewModel = orderViewModel,
                )

            }



            composable<Screens.Orders>(
                enterTransition = { return@composable fadeIn(tween(200)) },
                exitTransition = { return@composable fadeOut(tween(200)) }
            ) {
                OrdersScreen(
                    nav = nav,
                    orderViewModel = orderViewModel
                )

            }


        }


    }

}