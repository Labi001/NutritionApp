package com.labinot.bajrami.nutritionapp.presentation.screens.checkOut

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.nutritionapp.domain.helpers.Constants.EMAIL_TRIGGER_KEY
import com.labinot.bajrami.nutritionapp.domain.paymentProcess.PaypalApi
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.helpers.SupabaseFactory.client
import com.labinot.bajrami.nutritionapp.domain.models.CartItem
import com.labinot.bajrami.nutritionapp.domain.models.Country
import com.labinot.bajrami.nutritionapp.domain.models.Customer
import com.labinot.bajrami.nutritionapp.domain.models.Order
import com.labinot.bajrami.nutritionapp.domain.models.PhoneNumber
import com.labinot.bajrami.nutritionapp.domain.paymentProcess.Amount
import com.labinot.bajrami.nutritionapp.domain.paymentProcess.ShippingAddress
import com.labinot.bajrami.nutritionapp.domain.repository.CostumerRepository
import com.labinot.bajrami.nutritionapp.domain.repository.CountryRepository
import com.labinot.bajrami.nutritionapp.domain.repository.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class CheckoutScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val token: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val country: Country? = null,
    val phoneNumber: PhoneNumber? = null,
    val cart: List<CartItem> = emptyList(),
)



class CheckOutViewModel (
    private val customerRepository: CostumerRepository,
    private val orderRepository: OrderRepository,
    private val paypalApi: PaypalApi,
    private val countryRepository: CountryRepository,
    private val totalAmount: String,
) : ViewModel() {

    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: CheckoutScreenState by mutableStateOf(CheckoutScreenState())
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
            paypalApi.fetchAccessToken(
                onSuccess = { token ->
                    println("TOKEN RECEIVED: $token")
                    fetchToken(token)
                },
                onError = { message ->
                    println(message)
                }
            )
        }

        viewModelScope.launch {

            customerRepository.readCustomerFlow().collectLatest { data ->
                if (data.isSuccess()) {
                    val fetchedCustomer = data.getSuccessData()
                    screenState = CheckoutScreenState(
                        id = fetchedCustomer.id,
                        firstName = fetchedCustomer.firstName,
                        lastName = fetchedCustomer.lastName,
                        email = fetchedCustomer.email,
                        city = fetchedCustomer.city,
                        postalCode = fetchedCustomer.postalCode,
                        address = fetchedCustomer.address,
                        phoneNumber = fetchedCustomer.phoneNumber,
                        cart = fetchedCustomer.cart
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

    private fun syncSelectedCountry() {
        val dial = screenState.phoneNumber?.dialCode ?: return

        // Check if countries are actually loaded in our state
        val availableCountries = (countriesState as? RequestState.Success)?.data ?: return

        availableCountries.firstOrNull { it.dialCode == dial }?.let { match ->
            screenState = screenState.copy(country = match)
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

    fun fetchToken(value: String?) {
        screenState = screenState.copy(token = value)
    }



    fun updatePhoneNumber(value: String) {
        screenState = screenState.copy(
            phoneNumber = PhoneNumber(
                dialCode = screenState.country?.dialCode?:1,
                number = value
            )
        )
    }

    fun payOnDelivery(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        updateCustomer(
            onSuccess = {
                createTheOrder(
                    onSuccess = {
                        viewModelScope.launch {
                            try {
                                paypalApi.sendOrderEmail(
                                    orderType = "PayOnDelivery",
                                    customerName = "${screenState.firstName} ${screenState.lastName}",
                                    customerEmail = screenState.email,
                                    address = screenState.address ?: "No Address",
                                    postalCode = screenState.postalCode.toString(),
                                    phone = screenState.phoneNumber?.number ?: "No Tel",
                                    total = totalAmount,
                                    cartItems = screenState.cart
                                )
                            } finally {

                                onSuccess()
                            }

                        }

                    },
                    onError = onError
                )
            },
            onError = onError
        )


    }

    private fun updateCustomer(
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

    private fun createTheOrder(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            orderRepository.createTheOrder(
                order = Order(
                    customerId = screenState.id,
                    items = screenState.cart,
                    totalAmount = totalAmount.toDoubleOrNull()
                        ?: 0.0
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun payWithPayPal(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {

        viewModelScope.launch {
            paypalApi.beginCheckout(
                amount = Amount(
                    currencyCode = "USD",
                    value = totalAmount
                ),
                fullName = "${screenState.firstName} ${screenState.lastName}",
                shippingAddress = ShippingAddress(
                    addressLine1 = screenState.address ?: "Unknown address",
                    city = screenState.city ?: "Unknown city",
                    state = screenState.country?.name?:"Unknown country",
                    postalCode = screenState.postalCode.toString(),
                    countryCode = screenState.country?.code?:"Unknown code"
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }

        viewModelScope.launch {

            paypalApi.sendOrderEmail(
                orderType = "PayWithPaypal",
                customerName = screenState.firstName,
                customerEmail = screenState.email,
                address = screenState.address?:"No Address",
                postalCode = screenState.postalCode.toString(),
                phone = screenState.phoneNumber?.number?:"No Tel",
                total = totalAmount,
                cartItems = screenState.cart)
        }


    }



}