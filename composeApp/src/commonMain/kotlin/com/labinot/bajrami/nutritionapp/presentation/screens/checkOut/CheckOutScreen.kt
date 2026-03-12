package com.labinot.bajrami.nutritionapp.presentation.screens.checkOut

import ContentWithMessageBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.labinot.bajrami.nutritionapp.domain.helpers.BebasNeueFont
import com.labinot.bajrami.nutritionapp.domain.helpers.DisplayResult
import com.labinot.bajrami.nutritionapp.domain.helpers.FontSize
import com.labinot.bajrami.nutritionapp.domain.helpers.IconPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.Resources
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceBrand
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceError
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextWhite
import com.labinot.bajrami.nutritionapp.presentation.component.InfoCard
import com.labinot.bajrami.nutritionapp.presentation.component.LoadingCard
import com.labinot.bajrami.nutritionapp.presentation.component.PrimaryButton
import com.labinot.bajrami.nutritionapp.presentation.component.ProfileForm
import com.labinot.bajrami.nutritionapp.presentation.component.dialogs.CountryPickerDialog
import org.jetbrains.compose.resources.painterResource
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutScreen(navigator: AppNavigator,
                   totalAmount: Double,
                   viewModel: CheckOutViewModel){

    val messageBarState = rememberMessageBarState()
    val screenState = viewModel.screenState
    val isFormValid = viewModel.isFormValid
    val screenReady = viewModel.screenReady

    val countriesState = viewModel.countriesState
    var countryDialogOpen by remember { mutableStateOf(false) }

    ContentWithMessageBar(
        contentBackgroundColor = Surface,
        messageBarState = messageBarState,
        errorMaxLines = 2,
        errorContainerColor = SurfaceError,
        errorContentColor = TextWhite,
        successContainerColor = SurfaceBrand,
        successContentColor = TextPrimary
    ) {


        Column(modifier = Modifier.fillMaxSize()
            .background(Surface)
            .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top)
        {

            AnimatedVisibility(
                visible = countryDialogOpen
            ) {

                countriesState.DisplayResult(
                    onLoading = {LoadingCard(modifier = Modifier.fillMaxSize())},
                    onSuccess = { countries ->
                        CountryPickerDialog(
                            countries = countries,
                            selectedCountry = screenState.country,
                            onDismiss = {countryDialogOpen = false},
                            onConfirmClick = { selectedCountry ->
                                viewModel.updateCountry(selectedCountry)
                                countryDialogOpen = false
                            }
                        )

                    },
                    onError = { message ->
                        InfoCard(
                            image = Resources.Image.Cat,
                            title = "Oops!",
                            subtitle = message
                        )
                    }
                )




            }

            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "CHECKOUT",
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                },

                navigationIcon = {
                    IconButton(onClick = {navigator.popBack()}) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back Arrow icon",
                            tint = IconPrimary
                        )
                    }
                },

                actions = {

                    Text(
                        text = "$${totalAmount}",
                        fontSize = FontSize.EXTRA_MEDIUM,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(16.dp))


                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            screenReady.DisplayResult(

                onLoading = {LoadingCard(modifier = Modifier.fillMaxSize())},
               onSuccess = {

                   Column(modifier = Modifier.fillMaxSize()){

                       ProfileForm(
                           modifier = Modifier.weight(1f)
                               .padding(horizontal = 24.dp),
                           country = screenState.country,
                           onCountrySelect = { countryDialogOpen = true},
                           firstName = screenState.firstName,
                           onFirstNameChange = viewModel::updateFirstName,
                           lastName = screenState.lastName,
                           onLastNameChange = viewModel::updateLastName,
                           email = screenState.email,
                           city = screenState.city,
                           onCityChange = viewModel::updateCity,
                           postalCode = screenState.postalCode,
                           onPostalCodeChange = viewModel::updatePostalCode,
                           address = screenState.address,
                           onAddressChange = viewModel::updateAddress,
                           phoneNumber = screenState.phoneNumber?.number,
                           onPhoneNumberChange = viewModel::updatePhoneNumber
                       )

                       Column {
                           PrimaryButton(
                               text = "Pay with PayPal",
                               icon = Resources.Image.PaypalLogo,
                               enabled = isFormValid,
                               onClick = {
                                   viewModel.payWithPayPal(
                                       onSuccess = {
                                           navigator.navigateToPaymentCompleted(isSuccess = true,error = null,token = screenState.token)
                                       },
                                       onError = { message ->
                                           messageBarState.addError(message)
                                       }
                                   )
                               }
                           )
                           Spacer(modifier = Modifier.height(12.dp))
                           PrimaryButton(
                               text = "Pay on Delivery",
                               icon = Resources.Icon.ShoppingCart,
                               secondary = true,
                               enabled = isFormValid,
                               onClick = {
                                   viewModel.payOnDelivery(
                                       onSuccess = {
                                           navigator.navigateToPaymentCompleted(isSuccess = true,error = null,token = null)
                                       },
                                       onError = { message ->
                                           navigator.navigateToPaymentCompleted(isSuccess = null,error = message,token = null)
                                       }
                                   )
                               }
                           )
                           Spacer(modifier = Modifier.height(10.dp))
                       }



                   }



               },
                onError = { message ->

                    InfoCard(
                        image = Resources.Image.Cat,
                        title = "Oops!",
                        subtitle = message
                    )

                },


            )







        }




    }



}