package com.labinot.bajrami.nutritionapp.domain.NavSetup

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.repository.CostumerRepository
import com.labinot.bajrami.nutritionapp.presentation.screens.adminPanel.AdminPanelScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.adminPanel.manageProduct.ManageProductScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.adminPanel.manageProduct.ManageProductViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.auth.AuthScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.profile.ProfileScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NavGraphSetup() {

    val customerRepository = koinInject<CostumerRepository>()
    val isUserAuthenticated = remember { customerRepository.getCurrentUserId() != null }
    val startDestination = remember {
        if (isUserAuthenticated) NavRoutes.Main
        else NavRoutes.Auth
    }


//    var userId by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(Unit) {
//        // Small delay to ensure Firebase auth is initialized
//        delay(500)
//        userId = Firebase.auth.currentUser?.uid
//
//    }



    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(Home) }




        val backStack = rememberNavBackStack(
            configuration = SavedStateConfiguration {

                serializersModule = SerializersModule {

                    polymorphic(NavKey::class) {

                        subclass(NavRoutes.Auth::class, NavRoutes.Auth.serializer())
                        subclass(NavRoutes.Main::class, NavRoutes.Main.serializer())
                        subclass(NavRoutes.ProductsOverview::class, NavRoutes.ProductsOverview.serializer())
                        subclass(NavRoutes.CategorySearch::class, NavRoutes.CategorySearch.serializer())
                        subclass(NavRoutes.Profile::class, NavRoutes.Profile.serializer())
                        subclass(NavRoutes.AdminPanel::class, NavRoutes.AdminPanel.serializer())
                        subclass(NavRoutes.ManageProduct::class, NavRoutes.ManageProduct.serializer())
                        subclass(NavRoutes.Details::class, NavRoutes.Details.serializer())
                        subclass(NavRoutes.CheckOut::class, NavRoutes.CheckOut.serializer())
                        subclass(NavRoutes.PaymentCompleted::class, NavRoutes.PaymentCompleted.serializer()
                        )

                    }
                }
            },

            startDestination
        )


        val navigator = remember(key1 = topLevelBackStack)
        {

            object : AppNavigator {

                override fun navigateToAuth() {
                    backStack.clear()
                    backStack.add(NavRoutes.Auth)

                }

                override fun navigateToHome() {
                    backStack.add(NavRoutes.Main)
                    backStack.removeAll{it != NavRoutes.Main}
                    topLevelBackStack.switchTopLevel(Home)
                }

                override fun navigateToProductsOverview() {
                    topLevelBackStack.add(NavRoutes.ProductsOverview)
                }

                override fun navigateToCart() {
                    topLevelBackStack.add(Cart)
                }

                override fun navigateToCategories() {
                    topLevelBackStack.add(Category)
                }

                override fun navigateToCategorySearch(category: String) {
                    topLevelBackStack.add(NavRoutes.CategorySearch(category = category))
                }

                override fun navigateToProfile() {
                    backStack.add(NavRoutes.Profile)
                }

                override fun navigateToAdminPanel() {
                    backStack.add(NavRoutes.AdminPanel)
                }

                override fun navigateToManageProduct(id: String?) {
                    backStack.add(NavRoutes.ManageProduct(id = id))
                }

                override fun navigateToDetails(id: String) {
                    topLevelBackStack.add(NavRoutes.Details(id = id))
                }

                override fun navigateToCheckOut(totalAmount: String) {
                    topLevelBackStack.add(NavRoutes.CheckOut(totalAmount = totalAmount))
                }

                override fun navigateToPaymentCompleted(
                    isSuccess: Boolean?,
                    error: String?,
                    token: String?
                ) {
                    topLevelBackStack.add(
                        NavRoutes.PaymentCompleted(
                            isSuccess = isSuccess,
                            error = error,
                            token = token
                        )
                    )
                }

                override fun popBack() {

                    topLevelBackStack.removeLast()
                }

                override fun popToRoot() {

                    topLevelBackStack.resetTo(Home)

                }

                override fun navigateToMain() {

                    backStack.removeLast()
                    topLevelBackStack.switchTopLevel(Home)

                }

                override fun popBackMainNav() {
                    backStack.removeLast()
                }




            }


        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = Surface
        )
        { innerPadding ->


            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backStack,
                entryDecorators = listOf(

                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()

                ),
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {


                        entry<NavRoutes.Auth> {

                            AuthScreen(
                                navigator = navigator,
                                innerPadding
                            )

                        }


                    entry<NavRoutes.Main> {

                        MainNavSetup(
                            topLevelBackStack = topLevelBackStack,
                            navigator = navigator
                        )

                    }


                    entry<NavRoutes.Profile>(
                        metadata = NavDisplay.transitionSpec {


                            // Slide new content up, keeping the old content in place underneath
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                tween(300)
                            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                        } + NavDisplay.popTransitionSpec {
                            // Slide old content down, revealing the new content in place underneath
                            EnterTransition.None togetherWith
                                    slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        tween(300)
                                    )
                        } + NavDisplay.predictivePopTransitionSpec {
                            // Slide old content down, revealing the new content in place underneath
                            EnterTransition.None togetherWith
                                    slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        tween(300)
                                    )
                        }
                    )
                    {

                        ProfileScreen(navigator = navigator)


                    }



                    entry<NavRoutes.AdminPanel>(
                        metadata = NavDisplay.transitionSpec {
                            // Slide new content up, keeping the old content in place underneath
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(300)
                            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                        } + NavDisplay.popTransitionSpec {
                            // Slide old content down, revealing the new content in place underneath
                            EnterTransition.None togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(300)
                                    )
                        } + NavDisplay.predictivePopTransitionSpec {
                            // Slide old content down, revealing the new content in place underneath
                            EnterTransition.None togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                    ) {

                        AdminPanelScreen(navigator = navigator)

                    }

                    entry<NavRoutes.ManageProduct> { key ->

                        val vm: ManageProductViewModel = koinViewModel {
                            parametersOf(key.id)
                        }

                        ManageProductScreen(
                            navigator = navigator,
                            viewModel = vm,
                            id = key.id
                        )

                    }




                }
            )


        }





}

