package com.labinot.bajrami.nutritionapp.data

import com.labinot.bajrami.nutritionapp.data.repoImpl.AdminRepositoryImpl
import com.labinot.bajrami.nutritionapp.data.repoImpl.CustomerRepoImp
import com.labinot.bajrami.nutritionapp.data.repoImpl.OrderRepoImp
import com.labinot.bajrami.nutritionapp.data.repoImpl.ProductRepoImp
import com.labinot.bajrami.nutritionapp.domain.NavSetup.BottomNavViewModel
import com.labinot.bajrami.nutritionapp.domain.RestCountriesApi
import com.labinot.bajrami.nutritionapp.domain.paymentProcess.PaypalApi
import com.labinot.bajrami.nutritionapp.domain.repository.AdminRepository
import com.labinot.bajrami.nutritionapp.domain.repository.CostumerRepository
import com.labinot.bajrami.nutritionapp.domain.repository.CountryRepoImpl
import com.labinot.bajrami.nutritionapp.domain.repository.CountryRepository
import com.labinot.bajrami.nutritionapp.domain.repository.OrderRepository
import com.labinot.bajrami.nutritionapp.domain.repository.ProductRepository
import com.labinot.bajrami.nutritionapp.presentation.screens.checkOut.CheckOutViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.cart.CardScreenViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.detail.DetailViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.adminPanel.AdminPanelViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.auth.AuthViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.adminPanel.manageProduct.ManageProductViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.category.categorySearch.CategorySearchViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.home.HomeViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.paymentCompleted.PaymentViewModel
import com.labinot.bajrami.nutritionapp.presentation.screens.profile.ProfileViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf


val shareModule = module {

    single<CostumerRepository> { CustomerRepoImp() }
    single<AdminRepository> { AdminRepositoryImpl() }
    single<ProductRepository> { ProductRepoImp() }
    single<OrderRepository> { OrderRepoImp(get()) }
    single<PaypalApi> { PaypalApi() }
    single <RestCountriesApi> { RestCountriesApi() }
    single<CountryRepository> { CountryRepoImpl(get()) }



    viewModelOf(::AuthViewModel)
    viewModelOf(::BottomNavViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AdminPanelViewModel)
    viewModel{ parame ->
        ManageProductViewModel(
            adminRepository = get(),
            productId = parame.getOrNull<String>()
        )

    }
    viewModelOf(::HomeViewModel)
    viewModel{ parame ->
        DetailViewModel(
            productRepository = get(),
            customerRepository = get(),
            productId = parame.get<String>()
        )

    }

    viewModelOf(::CardScreenViewModel)

    viewModel{ parame ->

        CategorySearchViewModel(
            productRepository = get(),
            category = parame.getOrNull<String>()
        )

    }

    viewModel{ parame ->

        CheckOutViewModel(
            customerRepository = get(),
            orderRepository = get(),
            paypalApi = get(),
            countryRepository = get(),
            totalAmount = parame.get<String>()
        )

    }

    viewModel{ parame ->

        PaymentViewModel(
            customerRepository = get(),
            orderRepository = get(),
            productRepository = get(),
            isSuccessful = parame.getOrNull<Boolean>(),
            error = parame.getOrNull<String>(),
            token = parame.getOrNull<String>(),
        )

    }

}

expect val targetModule: Module

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null,
) {
    startKoin {
        config?.invoke(this)
       modules(shareModule,targetModule)
    }
}