package com.labinot.bajrami.nutritionapp.presentation.component

import MessageBarState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.labinot.bajrami.nutritionapp.domain.NavSetup.BottomNavItem
import com.labinot.bajrami.nutritionapp.domain.NavSetup.Cart
import com.labinot.bajrami.nutritionapp.domain.NavSetup.TopLevelBackStack
import com.labinot.bajrami.nutritionapp.domain.helpers.BebasNeueFont
import com.labinot.bajrami.nutritionapp.domain.helpers.CustomDrawerState
import com.labinot.bajrami.nutritionapp.domain.helpers.FontSize
import com.labinot.bajrami.nutritionapp.domain.helpers.IconPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.helpers.Resources
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.isOpened
import com.labinot.bajrami.nutritionapp.domain.models.Customer
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabWrapper(
    selectedDestination: NavKey,
    drawerState: CustomDrawerState,
    onMenuToggle: () -> Unit,
    topLevelBackStack: TopLevelBackStack<NavKey>,
    customer: RequestState<Customer>, // Assuming your data types
    totalAmount: RequestState<Double>,
    navigator: AppNavigator,
    messageBarState: MessageBarState,
    content: @Composable () -> Unit
) {

    val selectedDestination by remember {
        derivedStateOf { topLevelBackStack.topLevelKey }
    }



    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                AnimatedContent(targetState = selectedDestination,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith
                                fadeOut(tween(300))
                    }) { destination ->
                    Text(
                        text = (destination as BottomNavItem).title,
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                }
            },
            navigationIcon = {
                AnimatedContent(targetState = drawerState) { state ->
                    IconButton(onClick = onMenuToggle) {
                        Icon(
                            painter = painterResource(
                                if (state.isOpened()) Resources.Icon.Close else Resources.Icon.Menu
                            ),
                            contentDescription = null,
                            tint = IconPrimary
                        )
                    }
                }
            },
            actions = {
                AnimatedVisibility(visible = selectedDestination == Cart) {
                    if (customer.isSuccess() && customer.getSuccessData().cart.isNotEmpty()) {
                        IconButton(onClick = {
                            if (totalAmount.isSuccess()) {
                                navigator.navigateToCheckOut(totalAmount.getSuccessData().toString())
                            }
                        }) {
                            Icon(painterResource(Resources.Icon.RightArrow), null, tint = IconPrimary)
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
        )

        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}