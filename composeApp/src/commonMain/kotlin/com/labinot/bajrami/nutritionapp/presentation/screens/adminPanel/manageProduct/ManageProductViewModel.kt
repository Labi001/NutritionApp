package com.labinot.bajrami.nutritionapp.presentation.screens.adminPanel.manageProduct

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.models.Product
import com.labinot.bajrami.nutritionapp.domain.models.ProductCategory
import com.labinot.bajrami.nutritionapp.domain.repository.AdminRepository
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class ManageProductState(
    val id: String = Uuid.random().toHexString(),
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "thumbnail image",
    val selectedCategory: ProductCategory? = null,
    val allCategories: List<ProductCategory> = emptyList(),
    val isCategoryDialogOpen: Boolean = false,
    val flavors: String = "",
    val weight: Int? = null,
    val price: Double = 0.0,
    val isNew: Boolean = false,
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false
)

class ManageProductViewModel(
    private val adminRepository: AdminRepository,
     private val productId: String?
): ViewModel() {



    var screenState by mutableStateOf(ManageProductState())
        private set


    var thumbnailUploaderState: RequestState<Unit> by mutableStateOf(RequestState.Idle)
        private set

    val isFormValid: Boolean
        get() = screenState.title.isNotEmpty() &&
                screenState.description.isNotEmpty() &&
                screenState.thumbnail.isNotEmpty() &&
                screenState.price != 0.0

    init {

        screenState = screenState.copy(
            allCategories = ProductCategory.entries
        )

        if (!productId.isNullOrEmpty()) {
            viewModelScope.launch {
                val selectedProduct = adminRepository.readProductById(productId)
                if (selectedProduct.isSuccess()) {
                    val product = selectedProduct.getSuccessData()

                    updateId(product.id)
                    updateCreatedAt(product.createdAt)
                    updateTitle(product.title)
                    updateDescription(product.description)
                    updateThumbnail(product.thumbnail)
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                   onCategorySelected(ProductCategory.valueOf(product.category))
                    updateFlavors(product.flavors?.joinToString(",") ?: "")
                    updateWeight(product.weight)
                    updatePrice(product.price)
                    updateNew(product.isNew)
                    updatePopular(product.isPopular)
                    updateDiscounted(product.isDiscounted)
                }
            }
        }




    }


    fun updateId(value: String) {
        screenState = screenState.copy(id = value)
    }

    fun updateCreatedAt(value: Long) {
        screenState = screenState.copy(createdAt = value)
    }

    fun updateTitle(value: String) {
        screenState = screenState.copy(title = value)
    }

    fun updateDescription(value: String) {
        screenState = screenState.copy(description = value)
    }

    fun updateThumbnail(value: String) {
        screenState = screenState.copy(thumbnail = value)
    }

    fun updateThumbnailUploaderState(value: RequestState<Unit>) {
        thumbnailUploaderState = value
    }




    fun updateFlavors(value: String) {
        screenState = screenState.copy(flavors = value)
    }

    fun updateWeight(value: Int?) {
        screenState = screenState.copy(weight = value)
    }

    fun updatePrice(value: Double) {
        screenState = screenState.copy(price = value)
    }

    fun updateNew(value: Boolean) {
        screenState = screenState.copy(isNew = value)
    }

    fun updatePopular(value: Boolean) {
        screenState = screenState.copy(isPopular = value)
    }

    fun updateDiscounted(value: Boolean) {
        screenState = screenState.copy(isDiscounted = value)
    }


    fun onCategoryDialogDismiss(){
        screenState = screenState.copy(isCategoryDialogOpen = false)
    }

    fun onCategorySelected(category: ProductCategory) {
        screenState = screenState.copy(
            selectedCategory = category,
            isCategoryDialogOpen = false
        )
    }

    fun onCategoryFieldClick(){
        screenState = screenState.copy(isCategoryDialogOpen = true)
    }

    fun createNewProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            adminRepository.createNewProduct(
                product = Product(
                    id = screenState.id,
                    title = screenState.title,
                    description = screenState.description,
                    thumbnail = screenState.thumbnail,
                    category = screenState.selectedCategory?.name?:"No Category Selected",
                    flavors = screenState.flavors.split(","),
                    weight = screenState.weight,
                    price = screenState.price,
                    isNew = screenState.isNew,
                    isPopular = screenState.isPopular,
                    isDiscounted = screenState.isDiscounted
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun updateProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (isFormValid) {
            viewModelScope.launch {
                adminRepository.updateProduct(
                    product = Product(
                        id = screenState.id,
                        createdAt = screenState.createdAt,
                        title = screenState.title,
                        description = screenState.description,
                        thumbnail = screenState.thumbnail,
                        category = screenState.selectedCategory?.name?:"No category Selected",
                        flavors = screenState.flavors.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() },
                        weight = screenState.weight,
                        price = screenState.price,
                        isNew = screenState.isNew,
                        isPopular = screenState.isPopular,
                        isDiscounted = screenState.isDiscounted
                    ),
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        } else {
            onError("Please fill in the information.")
        }
    }



    @OptIn(ExperimentalUuidApi::class)
    fun uploadProductImage(byteArray: ByteArray,
                           onSuccess: () -> Unit,) {

        updateThumbnailUploaderState(RequestState.Loading)

        viewModelScope.launch {
            // 1. Generate a unique name for the file
            val fileName = "product_${Uuid.random().toHexString()}.jpg"

            // 2. Call the repository
            val imageUrl = adminRepository.uploadImage(fileName, byteArray)

            if (imageUrl != null) {

                if (!productId.isNullOrEmpty()) {

                    adminRepository.updateProductThumbnail(
                        productId = productId,
                        downloadUrl = imageUrl,
                        onSuccess = {
                            onSuccess()
                            updateThumbnailUploaderState(RequestState.Success(Unit))
                            updateThumbnail(imageUrl)
                        },
                        onError = { message ->
                            updateThumbnailUploaderState(RequestState.Error(message))
                        }
                    )


                }else {

                    onSuccess()
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                    updateThumbnail(imageUrl)

                }

            } else {
                // Error: Handle the failure
                updateThumbnailUploaderState(RequestState.Error("File is null. Error while selecting an image."))
            }
        }
    }

    fun deleteProductImage(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val urlToDelete = screenState.thumbnail



        viewModelScope.launch {
            adminRepository.deleteImageFromStorage(
                downloadUrl = urlToDelete,
                onSuccess = {

                    if (!productId.isNullOrEmpty()) {

                        viewModelScope.launch {

                            adminRepository.updateProductThumbnail(
                                productId = productId,
                                downloadUrl = "",
                                onSuccess = {

                                    updateThumbnail("")
                                    updateThumbnailUploaderState(RequestState.Idle)
                                    onSuccess()
                                },
                                onError = { message ->
                                    updateThumbnailUploaderState(RequestState.Error(message))
                                }
                            )

                        }


                    }else {
                        updateThumbnail("")
                        updateThumbnailUploaderState(RequestState.Idle)

                        // 2. Notify the UI that it's done
                        onSuccess()

                    }

                },
                onError = { errorMessage ->
                    // 3. Pass the error message back to the UI
                    onError(errorMessage)
                }
            )
        }
    }


    fun deleteProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        productId?.takeIf { it.isNotEmpty() }?.let { id ->
            viewModelScope.launch {
                adminRepository.deleteProduct(
                    productId = id,
                    onSuccess = {
                        deleteProductImage(
                            onSuccess = {},
                            onError = {}
                        )
                        onSuccess()
                    },
                    onError = { message -> onError(message) }
                )
            }
        }
    }




}


