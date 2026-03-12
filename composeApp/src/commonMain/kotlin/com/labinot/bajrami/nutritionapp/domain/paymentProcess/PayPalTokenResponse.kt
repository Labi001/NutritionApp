package com.labinot.bajrami.nutritionapp.domain.paymentProcess

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PayPalTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
)
