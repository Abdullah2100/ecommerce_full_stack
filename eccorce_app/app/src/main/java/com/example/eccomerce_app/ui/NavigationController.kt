package com.example.eccomerce_app.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.eccomerce_app.ui.OnBoarding.OnBoardingScreen
import com.example.eccomerce_app.ui.view.Auth.LoginScreen
import com.example.eccomerce_app.View.Pages.SignUpPage
import com.example.eccomerce_app.ui.view.account.AccountPage
import com.example.eccomerce_app.ui.view.account.ProfileScreen
import com.example.eccomerce_app.ui.view.account.store.StoreScreen
import com.example.eccomerce_app.ui.view.home.HomeAddressList
import com.example.eccomerce_app.ui.view.home.HomePage
import com.example.eccomerce_app.ui.view.location.LocationHomeScreen
import com.example.eccomerce_app.ui.view.location.LocationsList
import com.example.eccomerce_app.viewModel.HomeViewModel


@Composable
fun NavController(
    nav: NavHostController,
    authViewModle: AuthViewModel = koinViewModel(),
    homeViewModle: HomeViewModel = koinViewModel(),
    currentScreen: Int,
) {

    NavHost(
        startDestination = when (currentScreen) {
            1 -> Screens.OnBoarding
            2 -> Screens.AuthGraph
            3 -> Screens.LocationGraph
            else -> Screens.HomeGraph
        },
        navController = nav
    ) {

        composable<Screens.OnBoarding>(
            enterTransition = {
                return@composable fadeIn(tween(2000))
            },
            exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
                )
            },

            ) {
            OnBoardingScreen(nav, authViewModle)
        }

        navigation<Screens.LocationGraph>(
            startDestination = Screens.LocationHome
        ) {

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
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }
            ) {
                LocationHomeScreen(
                    nav = nav,
                    homeViewModle = homeViewModle
                )
            }

            composable<Screens.LocationList>(
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
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }
            ) {value->
                var isFromHomeLocation = value.toRoute<Screens.LocationList>()
                LocationsList(
                    nav = nav,
                    homeViewModle = homeViewModle,
                    isFromHome = isFromHomeLocation.isFromLocationHome
                )
            }

        }



        navigation<Screens.AuthGraph>(
            startDestination = Screens.Login
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
                    authKoin = authViewModle
                )
            }

            composable<Screens.Signup>(
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
            ) {
                SignUpPage(
                    nav = nav,
                    authKoin = authViewModle
                )
            }
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

            composable<Screens.HomeAddress>(
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
                HomeAddressList(
                    nav = nav,
                    homeViewModle = homeViewModle
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

            composable<Screens.Store>(
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
                StoreScreen(
                    nav = nav,
                    homeViewModel = homeViewModle
                )
            }

        }


    }

}