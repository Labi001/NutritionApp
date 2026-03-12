package com.labinot.bajrami.nutritionapp.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.nutritionapp.domain.repository.CostumerRepository
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class AuthViewModel(
    private val costumerRepository: CostumerRepository
): ViewModel() {

    fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            costumerRepository.createCustomer(
                user = user,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }


}