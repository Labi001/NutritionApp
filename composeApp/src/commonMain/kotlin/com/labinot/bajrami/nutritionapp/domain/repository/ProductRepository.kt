package com.labinot.bajrami.nutritionapp.domain.repository

import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.models.Product
import com.labinot.bajrami.nutritionapp.domain.models.ProductCategory
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getCurrentUserId(): String?
    fun readDiscountedProducts(): Flow<RequestState<List<Product>>>
    fun readNewProducts(): Flow<RequestState<List<Product>>>
    fun readProductByIdFlow(id: String): Flow<RequestState<Product>>
    fun readProductsByIdsFlow(ids: List<String>): Flow<RequestState<List<Product>>>
    fun readProductsByCategoryFlow(category: ProductCategory): Flow<RequestState<List<Product>>>


}