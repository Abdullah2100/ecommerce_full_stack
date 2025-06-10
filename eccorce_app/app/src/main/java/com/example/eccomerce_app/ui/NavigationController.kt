package com.example.e_commercompose.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import com.example.e_commercompose.viewModel.AuthViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.e_commercompose.ui.OnBoarding.OnBoardingScreen
import com.example.e_commercompose.ui.view.Auth.LoginScreen
import com.example.e_commercompose.View.Pages.SignUpPage
import com.example.e_commercompose.ui.view.Address.AddressScreen
import com.example.e_commercompose.ui.view.Address.MapScreen
import com.example.e_commercompose.ui.view.account.AccountPage
import com.example.e_commercompose.ui.view.account.ProfileScreen
import com.example.e_commercompose.ui.view.account.store.CreateProductScreen
import com.example.e_commercompose.ui.view.account.store.ProductDetail
import com.example.e_commercompose.ui.view.account.store.StoreScreen
import com.example.e_commercompose.ui.view.checkout.CheckoutScreen
import com.example.e_commercompose.ui.view.home.CartScreen
import com.example.e_commercompose.ui.view.home.HomePage
import com.example.e_commercompose.ui.view.home.OrderScreen
import com.example.e_commercompose.ui.view.location.LocationHomeScreen
import com.example.e_commercompose.ui.view.location.LocationsList
import com.example.e_commercompose.viewModel.HomeViewModel
import com.example.eccomerce_app.ui.view.account.OrderForMyStoreScreen


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
            ) {navRef->
                var store_id = navRef.toRoute<Screens.Store>()
                StoreScreen(
                    store_idCopy= store_id.store_idCopy,
                    isFromHome=store_id.isFromHome,
                    nav = nav,
                    homeViewModel = homeViewModle
                )
            }

            composable<Screens.CreateProduct>(

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
                navRef->

                var store = navRef.toRoute<Screens.CreateProduct>()

                CreateProductScreen(
                    nav = nav,
                    homeViewModel = homeViewModle,
                    storeId = store.store_id,
                    productId= store.product_id
                )

            }
            composable<Screens.ProductDetails>(

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
                    navRef->

                var store = navRef.toRoute<Screens.ProductDetails>()

                ProductDetail(
                    nav = nav,
                    homeViewModel = homeViewModle,
                    productID = store.product_Id,
                    isFromHome = store.isFromHome
                )

            }
            composable<Screens.Cart>(

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
                    navRef->


                CartScreen(
                    nav = nav,
                    homeViewModel = homeViewModle,
                )

            }

            composable<Screens.Checkout>(

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
                    navRef->


                CheckoutScreen(
                    nav = nav,
                    homeViewModel = homeViewModle,
                )

            }
            composable<Screens.Address>(

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
                AddressScreen(
                    nav = nav,
                    homeViewModle = homeViewModle
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

            composable<Screens.Order>(

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
                OrderScreen(
                    nav = nav,
                    homeViewModel = homeViewModle
                )

            }

            composable<Screens.OrderForMyStore>(

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
                OrderForMyStoreScreen(
                    nav = nav,
                    homeViewModel = homeViewModle
                )

            }


        }

    }

}