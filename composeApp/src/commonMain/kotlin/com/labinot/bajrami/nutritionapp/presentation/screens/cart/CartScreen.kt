package com.labinot.bajrami.nutritionapp.presentation.screens.cart

import ContentWithMessageBar
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.labinot.bajrami.nutritionapp.domain.helpers.DisplayResult
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.helpers.Resources
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceBrand
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceError
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextWhite
import com.labinot.bajrami.nutritionapp.presentation.component.CartItemCard
import com.labinot.bajrami.nutritionapp.presentation.component.InfoCard
import com.labinot.bajrami.nutritionapp.presentation.component.LoadingCard
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun CartScreen(){

    val messageBarState = rememberMessageBarState()
    val viewModel = koinViewModel<CardScreenViewModel>()
    val cartItemsWithProducts by viewModel.cartItemsWithProducts.collectAsState(RequestState.Loading)
    var listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()
        .background(Surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top)
    {


        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ){

            Spacer(modifier = Modifier.height(10.dp))

            cartItemsWithProducts.DisplayResult(
                onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
                onSuccess = { data ->
                    if (data.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            state = listState
                        ) {
                            items(
                                items = data,
                                key = { it.first.id }
                            ) { pair ->
                                CartItemCard(
                                    cartItem = pair.first,
                                    product = pair.second,
                                    onMinusClick = { quantity ->
                                        viewModel.updateCartItemQuantity(
                                            id = pair.first.id,
                                            quantity = quantity,
                                            onSuccess = {},
                                            onError = { messageBarState.addError(it) }
                                        )
                                    },
                                    onPlusClick = { quantity ->
                                        viewModel.updateCartItemQuantity(
                                            id = pair.first.id,
                                            quantity = quantity,
                                            onSuccess = {},
                                            onError = { messageBarState.addError(it) }
                                        )
                                    },
                                    onDeleteClick = {
                                        viewModel.deleteCartItem(
                                            id = pair.first.id,
                                            onSuccess = {},
                                            onError = { messageBarState.addError(it) }
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        InfoCard(
                            image = Resources.Image.ShoppingCart,
                            title = "Empty Cart",
                            subtitle = "Check some of our products."
                        )
                    }
                },
                onError = { message ->
                    InfoCard(
                        image = Resources.Image.Cat,
                        title = "Oops!",
                        subtitle = message
                    )
                },
                transitionSpec = fadeIn() togetherWith fadeOut()
            )





        }




    }




}