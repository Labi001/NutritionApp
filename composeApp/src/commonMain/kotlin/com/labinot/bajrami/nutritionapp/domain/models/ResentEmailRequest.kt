package com.labinot.bajrami.nutritionapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ResendEmailRequest(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String
)