package com.labinot.bajrami.nutritionapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {


    val products = combine(
        productRepository.readNewProducts(),
        productRepository.readDiscountedProducts()
    ) { new, discounted ->
        when {
            new.isSuccess() && discounted.isSuccess() -> {
                RequestState.Success(new.getSuccessData() + discounted.getSuccessData())
            }

            new.isError() -> new
            discounted.isError() -> discounted
            else -> RequestState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )



}