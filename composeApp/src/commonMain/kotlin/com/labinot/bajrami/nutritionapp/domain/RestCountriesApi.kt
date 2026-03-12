package com.labinot.bajrami.nutritionapp.domain

import com.labinot.bajrami.nutritionapp.domain.helpers.Constants.BASE_COUNTIES_API
import com.labinot.bajrami.nutritionapp.domain.models.RestCountriesDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RestCountriesApi {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true
            })
        }
    }

    suspend fun getAll(fields: String = "name,idd,flags,cca2"): List<RestCountriesDto> {
        return client.get(BASE_COUNTIES_API) {
            parameter("fields", fields)
        }.body()
    }


}