package com.labinot.bajrami.nutritionapp.domain.NavSetup

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.labinot.bajrami.nutritionapp.domain.helpers.Alpha
import com.labinot.bajrami.nutritionapp.domain.helpers.BorderIdle
import com.labinot.bajrami.nutritionapp.domain.helpers.CustomDrawerState
import com.labinot.bajrami.nutritionapp.domain.helpers.IconPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.IconSecondary
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceBrand
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceDarker
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceError
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceLighter
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextWhite
import com.labinot.bajrami.nutritionapp.domain.helpers.getScreenWidth
import com.labinot.bajrami.nutritionapp.domain.helpers.isOpened
import com.labinot.bajrami.nutritionapp.domain.helpers.opposite
import com.labinot.bajrami.nutritionapp.domain.helpers.utils.PreferenceRepository
import com.labinot.bajrami.nutritionapp.presentation.component.CustomDrawer
import com.labinot.bajrami.nutritionapp.presentation.component.MainTabWrapper
import com.labinot.bajrami.nutritionapp.presentation.screens.cart.CartScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.category.CategoryScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.category.categorySearch.CategorySearch
import com.labinot.bajrami.nutritionapp.presentation.screens.category.categorySearch.CategorySearchViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.checkOut.CheckOutScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.checkOut.CheckOutViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.detail.DetailScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.detail.DetailViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.home.HomeScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.paymentCompleted.PaymentCompletedScreen
import com.labinot.bajrami.nutritionapp.presentation.screens.paymentCompleted.PaymentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavSetup(
    topLevelBackStack: TopLevelBackStack<NavKey>,
    navigator: AppNavigator
) {

    val bottomNavItems = listOf(Home, Cart, Category)

    val scope = rememberCoroutineScope()



    val isBottomBarVisible by remember {
        derivedStateOf {
            // Get the actual key of the screen currently being displayed
            // Only show if the current active screen is one of our root tabs
            val currentKey = topLevelBackStack.backStack.lastOrNull()
            currentKey == Home || currentKey == Cart || currentKey == Category
        }
    }



    val preferenceData by PreferenceRepository.readPayPalDataFlow()
        .collectAsState(initial = null)

    LaunchedEffect(preferenceData) {
        // Only navigate if we have actual data to show
        if (preferenceData?.isSuccess != null || preferenceData?.error != null) {
            navigator.navigateToPaymentCompleted(
                isSuccess = preferenceData!!.isSuccess,
                error = preferenceData!!.error,
                token = preferenceData!!.token
            )
            // After navigating, we clear the data so the Flow emits 'null'
            PreferenceRepository.reset()
        }
    }


    val screenWidth = remember { getScreenWidth() }
    val viewModel = koinViewModel<BottomNavViewModel>()
    val customer by viewModel.customer.collectAsState()
    val totalAmount by viewModel.totalAmountFlow.collectAsState(RequestState.Loading)


    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }

    val offsetValue by remember { derivedStateOf { (screenWidth / 1.5).dp } }
    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
    )

    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) SurfaceLighter else Surface
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )

    val animatedRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )

    val messageBarState = rememberMessageBarState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(animatedBackground)
        .systemBarsPadding())
    {


        CustomDrawer(
            customer = customer,
            onProfileClick = {

                scope.launch {

                    drawerState = drawerState.opposite()
                    delay(300)
                    navigator.navigateToProfile()

                }

            },
            onContactUsClick = {},
            onSignOutClick = {

                viewModel.signOut(
                    onSuccess = { navigator.navigateToAuth() },
                    onError = {message -> messageBarState.addError(message)}
                )

            },
            onAdminPanelClick = {
                scope.launch {

                    drawerState = drawerState.opposite()
                    delay(300)
                    navigator.navigateToAdminPanel()

                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size = animatedRadius))
                .offset(x = animatedOffset)
                .scale(scale = animatedScale)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(size = animatedRadius),
                    ambientColor = Color.Black.copy(alpha = Alpha.DISABLED),
                    spotColor = Color.Black.copy(alpha = Alpha.DISABLED)
                )
        ) {

            Scaffold(modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = Surface
            ){ innerPadding ->

                ContentWithMessageBar(
                    contentBackgroundColor = Surface,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding()
                        ),
                    messageBarState = messageBarState,
                    errorMaxLines = 2,
                    errorContainerColor = SurfaceError,
                    errorContentColor = TextWhite,
                    successContainerColor = SurfaceBrand,
                    successContentColor = TextPrimary
                ) {

                    Box(modifier = Modifier.fillMaxSize()
                        .background(Surface),
                        contentAlignment = Alignment.TopStart)
                    {


                        NavDisplay(
                            modifier = Modifier.fillMaxSize(),
                            backStack = topLevelBackStack.backStack,
                            onBack = {topLevelBackStack.removeLast()},
                            entryDecorators = listOf(

                               rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()

                            ),
                            entryProvider = entryProvider {


                                entry<Home> {

                                    MainTabWrapper(Home, drawerState, { drawerState = drawerState.opposite() },
                                        topLevelBackStack, customer, totalAmount, navigator, messageBarState) {
                                        HomeScreen(navigator = navigator)
                                    }

                                }


                                entry<Cart> {

                                    MainTabWrapper(Cart, drawerState, { drawerState = drawerState.opposite() },
                                        topLevelBackStack, customer, totalAmount, navigator, messageBarState) {
                                        CartScreen()
                                    }

                                }

                                entry<Category> {

                                    MainTabWrapper(Category, drawerState, { drawerState = drawerState.opposite() },
                                        topLevelBackStack, customer, totalAmount, navigator, messageBarState) {
                                        CategoryScreen(navigator = navigator)
                                    }



                                }


                                entry<NavRoutes.ProductsOverview> {



                                }


                                entry<NavRoutes.CategorySearch>(
                                    metadata = NavDisplay.transitionSpec {
                                        // Slide new content up, keeping the old content in place underneath
                                        slideInHorizontally(
                                            initialOffsetX = { it },
                                            animationSpec = tween(400)
                                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                                    } + NavDisplay.popTransitionSpec {
                                        // Slide old content down, revealing the new content in place underneath
                                        EnterTransition.None togetherWith
                                                slideOutHorizontally(
                                                    targetOffsetX = { it },
                                                    animationSpec = tween(400)
                                                )
                                    } + NavDisplay.predictivePopTransitionSpec {
                                        // Slide old content down, revealing the new content in place underneath
                                        EnterTransition.None togetherWith
                                                slideOutHorizontally(
                                                    targetOffsetX = { it },
                                                    animationSpec = tween(400)
                                                )
                                    }
                                ) { key ->

                                    val cvm: CategorySearchViewModel = koinViewModel {
                                        parametersOf(key.category)
                                    }

                                    CategorySearch(navigator = navigator,
                                                  viewModel = cvm,
                                        categoryTitle = key.category)


                                }

                                entry<NavRoutes.CheckOut>(
                                    metadata = NavDisplay.transitionSpec {
                                        // Slide new content up, keeping the old content in place underneath
                                        slideInHorizontally(
                                            initialOffsetX = { it },
                                            animationSpec = tween(400)
                                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                                    } + NavDisplay.popTransitionSpec {
                                        // Slide old content down, revealing the new content in place underneath
                                        EnterTransition.None togetherWith
                                                slideOutHorizontally(
                                                    targetOffsetX = { it },
                                                    animationSpec = tween(400)
                                                )
                                    } + NavDisplay.predictivePopTransitionSpec {
                                        // Slide old content down, revealing the new content in place underneath
                                        EnterTransition.None togetherWith
                                                slideOutHorizontally(
                                                    targetOffsetX = { it },
                                                    animationSpec = tween(400)
                                                )
                                    }
                                ) { key ->

                                    val chvm: CheckOutViewModel = koinViewModel {
                                        parametersOf(key.totalAmount)
                                    }

                                    CheckOutScreen(navigator = navigator,
                                        totalAmount = key.totalAmount.toDoubleOrNull() ?: 0.0,
                                        viewModel = chvm)


                                }


                                entry<NavRoutes.Details>(
                                    metadata = NavDisplay.transitionSpec {
                                        // Slide new content up, keeping the old content in place underneath
                                        slideInHorizontally(
                                            initialOffsetX = { it },
                                            animationSpec = tween(400)
                                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                                    } + NavDisplay.popTransitionSpec {
                                        // Slide old content down, revealing the new content in place underneath
                                        EnterTransition.None togetherWith
                                                slideOutHorizontally(
                                                    targetOffsetX = { it },
                                                    animationSpec = tween(400)
                                                )
                                    } + NavDisplay.predictivePopTransitionSpec {
                                        // Slide old content down, revealing the new content in place underneath
                                        EnterTransition.None togetherWith
                                                slideOutHorizontally(
                                                    targetOffsetX = { it },
                                                    animationSpec = tween(400)
                                                )
                                    }
                                ) { key ->


                                    val dvm: DetailViewModel = koinViewModel {
                                        parametersOf(key.id)
                                    }

                                    DetailScreen(appNavigator = navigator,
                                        viewModel = dvm)

                                }

                                entry<NavRoutes.PaymentCompleted>(
                                    metadata = NavDisplay.transitionSpec {

                                        slideInVertically(
                                            initialOffsetY = { it },
                                            animationSpec = tween(400)
                                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                                    } + NavDisplay.popTransitionSpec {

                                        EnterTransition.None togetherWith
                                                slideOutVertically(
                                                    targetOffsetY = { it },
                                                    animationSpec = tween(400)
                                                )
                                    } + NavDisplay.predictivePopTransitionSpec {

                                        EnterTransition.None togetherWith
                                                slideOutVertically(
                                                    targetOffsetY = { it },
                                                    animationSpec = tween(400)
                                                )
                                    }
                                ) { key ->

                                    val pvm: PaymentViewModel = koinViewModel {
                                        parametersOf(key.isSuccess,key.error,key.token)
                                    }

                                    PaymentCompletedScreen(
                                        viewModel = pvm,
                                        navigator = navigator)

                                }



                            }
                        )




                        AnimatedVisibility(
                            modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                            .navigationBarsPadding(),
                            visible = isBottomBarVisible,
                            enter = slideInVertically(tween(durationMillis = 600)) { h -> h},
                            exit = slideOutVertically(tween(durationMillis = 600)) { h -> h}
                        ) {

                            Box(modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(
                                    vertical = 10.dp,
                                    horizontal = 26.dp
                                )
                                .clip(RoundedCornerShape(size = 12.dp))
                                .border(
                                    width = 1.dp,
                                    color = BorderIdle,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(SurfaceDarker),
                                contentAlignment = Alignment.Center)
                            {


                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(67.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ){

                                    bottomNavItems.forEachIndexed  { index , item ->

                                        val selected = topLevelBackStack.topLevelKey == item

                                        val animatedTint by animateColorAsState(
                                            targetValue = if (selected) IconSecondary else IconPrimary
                                        )

                                        if(index == 1){

                                            Box(contentAlignment = Alignment.TopEnd) {

                                                AnimatedContent(
                                                    targetState = customer
                                                ) { customerState ->
                                                    if (customerState.isSuccess() && customerState.getSuccessData().cart.isNotEmpty()) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(8.dp)
                                                                .offset(x = 68.dp, y = (-12).dp)
                                                                .clip(CircleShape)
                                                                .background(IconSecondary)
                                                        )
                                                    }
                                                }



                                            }



                                        }

                                        NavigationBarItem(
                                            selected = selected,
                                            icon = {
                                                Icon(
                                                    painter = painterResource(resource = item.icon),
                                                    contentDescription = item.title,
                                                    tint = animatedTint
                                                )
                                            },

                                            onClick = {

                                                topLevelBackStack.switchTopLevel(item)


                                            },



                                        )



//                        Box(contentAlignment = Alignment.TopEnd) {
//                            Icon(
//                                modifier = Modifier.clickable {
//
//                                    topLevelBackStack.switchTopLevel(item)
//                                },
//                                painter = painterResource(item.icon),
//                                contentDescription = "Bottom Bar destination icon",
//                                tint = animatedTint
//                            )
//                            if (item == Cart) {
//                                //  AnimatedContent(
//                                //     targetState = customer
//                                // ) { customerState ->
//                                // if (customerState.isSuccess() && customerState.getSuccessData().cart.isNotEmpty()) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(8.dp)
//                                        .offset(x = 4.dp, y = (-4).dp)
//                                        .clip(CircleShape)
//                                        .background(IconSecondary)
//                                )
//                            }
//                            // }
//                        }
                                    }




                                }




                            }




                        }



                    }


                }




            }





        }




    }







}