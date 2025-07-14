package com.example.e_commerc_delivery_man.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.viewModel.AuthViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.e_commerc_delivery_man.ui.view.Auth.LoginScreen
import com.example.e_commerc_delivery_man.ui.view.Address.AddressScreen
import com.example.e_commerc_delivery_man.ui.view.Address.MapScreen
import com.example.e_commerc_delivery_man.ui.view.account.AccountPage
import com.example.e_commerc_delivery_man.ui.view.account.ProfileScreen
import com.example.e_commerc_delivery_man.ui.view.account.store.CreateProductScreen
import com.example.e_commerc_delivery_man.ui.view.account.store.ProductDetail
import com.example.e_commerc_delivery_man.ui.view.account.store.StoreScreen
import com.example.e_commerc_delivery_man.ui.view.checkout.CheckoutScreen
import com.example.e_commerc_delivery_man.ui.view.home.MyOrdersScreen
import com.example.e_commerc_delivery_man.ui.view.home.HomePage
import com.example.e_commerc_delivery_man.ui.view.home.OrdersScreen
import com.example.e_commerc_delivery_man.ui.view.home.ProductCategoryScreen
import com.example.e_commerc_delivery_man.viewModel.HomeViewModel
import com.example.eccomerce_app.ui.view.home.CategoryScreen


@Composable
fun NavController(
    nav: NavHostController,
    authViewModle: AuthViewModel = koinViewModel(),
    homeViewModle: HomeViewModel = koinViewModel(),
    currentScreen: Int,
) {

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

            composable<Screens.Category>(
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
                CategoryScreen(
                    nav = nav,
                    homeViewModel = homeViewModle
                )
            }

            composable<Screens.ProductCategory>(
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
            ) {result->
                val data =result.toRoute<Screens.ProductCategory>()
                ProductCategoryScreen(
                    nav = nav,
                    homeViewModel = homeViewModle,
                    category_id = data.cateogry_id
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
            ) { navRef ->
                var store_id = navRef.toRoute<Screens.Store>()
                StoreScreen(
                    store_idCopy = store_id.store_id,
                    isFromHome = store_id.isFromHome,
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
            ) { navRef ->

                var store = navRef.toRoute<Screens.CreateProduct>()

                CreateProductScreen(
                    nav = nav,
                    homeViewModel = homeViewModle,
                    storeId = store.store_id,
                    productId = store.product_id
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
            ) { navRef ->

                var store = navRef.toRoute<Screens.ProductDetails>()

                ProductDetail(
                    nav = nav,
                    homeViewModel = homeViewModle,
                    productID = store.product_Id,
                    isFromHome = store.isFromHome
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
            ) { navRef ->


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