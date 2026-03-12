package com.labinot.bajrami.nutritionapp.domain.repository

import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.models.CartItem
import com.labinot.bajrami.nutritionapp.domain.models.Customer
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface CostumerRepository {


    fun getCurrentUserId(): String?
    suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )
    fun readCustomerFlow(): Flow<RequestState<Customer>>
    suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun addItemToCard(
        cartItem: CartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun updateCartItemQuantity(
        id: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun deleteCartItem(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun deleteAllCartItems(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun signOut(): RequestState<Unit>



}