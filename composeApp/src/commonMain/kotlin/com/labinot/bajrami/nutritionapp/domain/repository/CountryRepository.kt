package com.labinot.bajrami.nutritionapp.domain.repository

import com.labinot.bajrami.nutritionapp.domain.RestCountriesApi
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.models.Country
import com.labinot.bajrami.nutritionapp.domain.models.toCountryOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

interface CountryRepository {

    suspend fun fetchCountries() : Flow<RequestState<List<Country>>>

}

class CountryRepoImpl(
    private val api: RestCountriesApi
): CountryRepository{


    override suspend fun fetchCountries(): Flow<RequestState<List<Country>>> = flow{

        try {
            emit(RequestState.Loading)
            val countries = withContext(Dispatchers.IO){
                api.getAll()
                    .mapNotNull { it.toCountryOrNull() }
                    .distinctBy { it.code }
                    .sortedBy { it.name }
            }
            emit(RequestState.Success(countries))
        } catch (e: Exception) {
            emit(RequestState.Error("Cannot access the API endpoint: ${e.message ?: "Unknown error"}"))
        }


    }


}