package com.labinot.bajrami.nutritionapp.domain.repository

import com.labinot.bajrami.nutritionapp.domain.models.Order

interface OrderRepository {

    fun getCurrentUserId(): String?
    suspend fun createTheOrder(
        order: Order,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )


}