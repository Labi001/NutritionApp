package com.labinot.bajrami.nutritionapp.presentation.screens.auth

import ContentWithMessageBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.labinot.bajrami.nutritionapp.domain.helpers.Alpha
import com.labinot.bajrami.nutritionapp.domain.helpers.BebasNeueFont
import com.labinot.bajrami.nutritionapp.domain.helpers.FontSize
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceBrand
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceError
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextSecondary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextWhite
import com.labinot.bajrami.nutritionapp.presentation.component.GoogleButton
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Suppress("SuspiciousIndentation")
@Composable
fun AuthScreen(navigator: AppNavigator,
               innerPadding: PaddingValues){

    val viewModel = koinViewModel<AuthViewModel>()

    val messageBarState = rememberMessageBarState()
    var loadingState by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()


        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .systemBarsPadding(),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ){

            Column( modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp)
            ){

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "NUTRISPORT",
                        textAlign = TextAlign.Center,
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.EXTRA_LARGE,
                        color = TextSecondary
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(Alpha.HALF),
                        text = "Sign in to continue",
                        textAlign = TextAlign.Center,
                        fontSize = FontSize.EXTRA_REGULAR,
                        color = TextPrimary
                    )
                }

                GoogleButtonUiContainerFirebase(
                    linkAccount = false,
                    onResult = { result ->

                        result.onSuccess { user ->

                            messageBarState.addSuccess("Authentication successful!")

                            viewModel.createCustomer(
                                user = user,
                                onSuccess = {
                                    scope.launch {
                                        messageBarState.addSuccess("Authentication successful!")
                                        delay(2000)
                                       navigator.navigateToHome()
                                    }
                                },
                                onError = { message -> messageBarState.addError(message) }
                            )
                            loadingState = false
                        }.onFailure { error ->
                            if (error.message?.contains("A network error") == true) {
                                messageBarState.addError("Internet connection unavailable.")
                            } else if (error.message?.contains("Idtoken is null") == true) {
                                messageBarState.addError("Sign in canceled.")
                            } else {
                                messageBarState.addError(error.message ?: "Unknown")
                            }
                            loadingState = false
                        }

                    }
                ){
                    GoogleButton(loading = loadingState) {
                        loadingState = true
                        this@GoogleButtonUiContainerFirebase.onClick()
                    }

                }





            }



        }




}


