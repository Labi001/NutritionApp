package com.labinot.bajrami.nutritionapp.presentation.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.models.CartItem
import com.labinot.bajrami.nutritionapp.domain.repository.CostumerRepository
import com.labinot.bajrami.nutritionapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel (
    private val productRepository: ProductRepository,
    private val customerRepository: CostumerRepository,
    private val productId: String,
): ViewModel() {

    val product = productRepository.readProductByIdFlow(
        productId
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

    var quantity by mutableStateOf(1)
        private set

    var selectedFlavor: String? by mutableStateOf(null)
        private set

    fun updateQuantity(value: Int) {
        quantity = value
    }

    fun updateFlavor(value: String) {
        selectedFlavor = value
    }


    fun addItemToCart(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {

            customerRepository.addItemToCard(
                cartItem = CartItem(
                    productId = productId,
                    flavor = selectedFlavor,
                    quantity = quantity
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }




}