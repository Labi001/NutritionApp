package com.labinot.bajrami.nutritionapp.domain.helpers.utils

import com.labinot.bajrami.nutritionapp.domain.NavSetup.NavRoutes
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getBooleanOrNullFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.observable.makeObservable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine

object PreferenceRepository {

    @OptIn(ExperimentalSettingsApi::class)
    private val settings: ObservableSettings = Settings().makeObservable()

    private const val IS_SUCCESS = "isSuccess_paypal"
    private const val ERROR = "error_paypal"
    private const val TOKEN = "token_paypal"

    fun savePayPalData(
        isSuccess: Boolean?,
        error: String?,
        token: String?,
    ) {
        // We only save if the value isn't null to avoid overwriting with empty data
        isSuccess?.let { settings.putBoolean(IS_SUCCESS, it) }
        error?.let { settings.putString(ERROR, it) }
        token?.let { settings.putString(TOKEN, it) }
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun readPayPalDataFlow(): Flow<NavRoutes.PaymentCompleted?> = combine(
        settings.getBooleanOrNullFlow(IS_SUCCESS),
        settings.getStringOrNullFlow(ERROR),
        settings.getStringOrNullFlow(TOKEN)
    ) { isSuccess, error, token ->
        // Logic: If we have an explicit success OR an explicit error message,
        // then we have a valid payment result to show.
        if (isSuccess != null || error != null) {
            NavRoutes.PaymentCompleted(
                isSuccess = isSuccess,
                error = error,
                token = token
            )
        } else {
            // If everything is null (after a reset), emit null so the UI stops navigating
            null
        }
    }

    fun reset() {
        settings.remove(IS_SUCCESS)
        settings.remove(ERROR)
        settings.remove(TOKEN)
    }

}