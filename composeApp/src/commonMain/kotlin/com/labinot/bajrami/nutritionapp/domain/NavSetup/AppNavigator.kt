package com.labinot.bajrami.nutritionapp.domain.NavSetup

interface AppNavigator {

    fun navigateToAuth()

    fun navigateToHome()

    fun navigateToProductsOverview()

    fun navigateToCart()

    fun navigateToCategories()

    fun navigateToCategorySearch(category: String)

    fun navigateToProfile()

    fun navigateToAdminPanel()

    fun navigateToManageProduct(id: String? = null)

    fun navigateToDetails(id: String)

    fun navigateToCheckOut(totalAmount: String)

    fun navigateToPaymentCompleted(isSuccess: Boolean? = null,error: String? = null,token: String? = null)

    fun popBack()

    fun popToRoot()

    fun navigateToMain()

    fun popBackMainNav()


}