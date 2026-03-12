package com.labinot.bajrami.nutritionapp.presentation.screens.paymentCompleted

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.labinot.bajrami.nutritionapp.domain.helpers.DisplayResult
import com.labinot.bajrami.nutritionapp.domain.helpers.Resources
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.presentation.component.InfoCard
import com.labinot.bajrami.nutritionapp.presentation.component.LoadingCard
import com.labinot.bajrami.nutritionapp.presentation.component.PrimaryButton

@Composable
fun PaymentCompletedScreen(
   viewModel: PaymentViewModel,
    navigator: AppNavigator
){

    val screenState = viewModel.screenState

    Column(modifier = Modifier.fillMaxSize()
        .background(Surface)
        .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally)
    {


        screenState.DisplayResult(
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = {
                Column {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        InfoCard(
                            title = "Success!",
                            subtitle = "Your purchase is on the way.",
                            image = Resources.Image.Checkmark
                        )
                    }
                    PrimaryButton(
                        text = "Go back",
                        icon = Resources.Icon.RightArrow,
                        onClick = {
                            navigator.popToRoot()
                        }
                    )
                }
            },
            onError = { message ->
                Column {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        InfoCard(
                            title = "Oops!",
                            subtitle = message,
                            image = Resources.Image.Cat
                        )
                    }
                    PrimaryButton(
                        text = "Go back",
                        icon = Resources.Icon.RightArrow,
                        onClick = {
                            navigator.popToRoot()
                        }
                    )
                }
            }
        )






    }




}