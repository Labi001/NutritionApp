package com.labinot.bajrami.nutritionapp.presentation.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.models.Country
import com.labinot.bajrami.nutritionapp.domain.models.Customer
import com.labinot.bajrami.nutritionapp.domain.models.PhoneNumber
import com.labinot.bajrami.nutritionapp.domain.repository.CostumerRepository
import com.labinot.bajrami.nutritionapp.domain.repository.CountryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


data class ProfileScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val country: Country? = null,
    val phoneNumber: PhoneNumber? = null,
)


class ProfileViewModel(
     private val customerRepository: CostumerRepository,
     private val countryRepository: CountryRepository,

 ): ViewModel() {

    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: ProfileScreenState by mutableStateOf(ProfileScreenState())
        private set

    var countriesState by mutableStateOf<RequestState<List<Country>>>(RequestState.Loading)
        private set

    private var countries: List<Country> = emptyList()


    val isFormValid: Boolean
        get() = with(screenState) {
            firstName.length in 3..50 &&
                    lastName.length in 3..50 &&
                    city?.length in 3..50 &&
                    postalCode != null || postalCode?.toString()?.length in 3..8 &&
                    address?.length in 3..50 &&
                    phoneNumber?.number?.length in 5..30
        }



    init {

         loadCountries()

        viewModelScope.launch {


            customerRepository.readCustomerFlow().collectLatest { data ->
                if (data.isSuccess()) {
                    val fetchedCustomer = data.getSuccessData()
                    screenState = ProfileScreenState(
                        id = fetchedCustomer.id,
                        firstName = fetchedCustomer.firstName,
                        lastName = fetchedCustomer.lastName,
                        email = fetchedCustomer.email,
                        city = fetchedCustomer.city,
                        postalCode = fetchedCustomer.postalCode,
                        address = fetchedCustomer.address,
                        phoneNumber = fetchedCustomer.phoneNumber,

                    )
                    syncSelectedCountry()
                    delay(150)
                    screenReady = RequestState.Success(Unit)
                } else if (data.isError()) {
                    screenReady = RequestState.Error(data.getErrorMessage())
                }
            }
        }


    }

    private fun loadCountries() = viewModelScope.launch   {
        countryRepository.fetchCountries()
            .onStart { countriesState = RequestState.Loading }
            .collect { state ->
                countriesState = state
                if (state is RequestState.Success){
                    countries = state.data

                    screenState.phoneNumber?.dialCode?.let { dial ->
                        state.data.firstOrNull { it.dialCode == dial }?. let { match ->
                            screenState = screenState.copy(country = match)
                        }
                    }
                }
            }
    }

    fun updateCountry(value: Country) {
        screenState = screenState.copy(
            country = value,
            phoneNumber = screenState.phoneNumber?.copy(
                dialCode = value.dialCode
            ) ?: PhoneNumber(dialCode = value.dialCode, number = screenState.phoneNumber?.number ?: "")
        )
    }

    private fun syncSelectedCountry() {
        val dial = screenState.phoneNumber?.dialCode ?: return

        // Check if countries are actually loaded in our state
        val availableCountries = (countriesState as? RequestState.Success)?.data ?: return

        availableCountries.firstOrNull { it.dialCode == dial }?.let { match ->
            screenState = screenState.copy(country = match)
        }
    }



    fun updateFirstName(value: String) {
        screenState = screenState.copy(firstName = value)
    }

    fun updateLastName(value: String) {
        screenState = screenState.copy(lastName = value)
    }

    fun updateCity(value: String) {
        screenState = screenState.copy(city = value)
    }

    fun updatePostalCode(value: Int?) {
        screenState = screenState.copy(postalCode = value)
    }

    fun updateAddress(value: String) {
        screenState = screenState.copy(address = value)
    }


    fun updatePhoneNumber(value: String) {
        screenState = screenState.copy(
            phoneNumber = PhoneNumber(
                dialCode = screenState.country?.dialCode?: 1,
                number = value
            )
        )
    }


    fun updateCustomer(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            customerRepository.updateCustomer(
                customer = Customer(
                    id = screenState.id,
                    firstName = screenState.firstName,
                    lastName = screenState.lastName,
                    email = screenState.email,
                    city = screenState.city,
                    postalCode = screenState.postalCode,
                    address = screenState.address,
                    phoneNumber = screenState.phoneNumber
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }




}
