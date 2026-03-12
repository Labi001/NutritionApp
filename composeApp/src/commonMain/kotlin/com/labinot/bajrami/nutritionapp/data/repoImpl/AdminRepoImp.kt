package com.labinot.bajrami.nutritionapp.data.repoImpl

import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.helpers.SupabaseFactory
import com.labinot.bajrami.nutritionapp.domain.models.Product
import com.labinot.bajrami.nutritionapp.domain.repository.AdminRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withTimeout
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AdminRepositoryImpl : AdminRepository {




    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val firestore = Firebase.firestore
                val productCollection = firestore.collection(collectionPath = "product")
                productCollection.document(product.id)
                    .set(product.copy(title = product.title.lowercase()))
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while creating a new product: ${e.message}")
        }


    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun uploadImageToStorage(file: File): String? {
        return if (getCurrentUserId() != null) {
            val storage = Firebase.storage.reference
            val imagePath = storage.child(path = "images/${Uuid.random().toHexString()}")
            try {
                withTimeout(timeMillis = 20000L) {
                    imagePath.putFile(file)
                    imagePath.getDownloadUrl()
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    override suspend fun deleteImageFromStorage(
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        try {

            val fileName = downloadUrl.substringAfterLast("/")

            // 2. Stronger Validation: Check for blank names AND ensure it's a valid Supabase HTTPS link
            if (fileName.isBlank() || !downloadUrl.startsWith("https://")) {
                onError("Invalid image URL: Path is either empty or not a secure remote link.")
                return
            }

            // 2. Call Supabase to remove the file
            SupabaseFactory.client.storage
                .from("NutrionAppImages")
                .delete(fileName)

            onSuccess()

        } catch (e: Exception) {
            // 4. Trigger error callback with the exception message
            onError(e.message ?: "Unknown error occurred while deleting")
        }


    }


    private fun extractFirebaseStoragePath(downloadUrl: String): String? {
        val startIndex = downloadUrl.indexOf("/o/") + 3
        if (startIndex < 3) return null

        val endIndex = downloadUrl.indexOf("?", startIndex)
        val encodedPath = if (endIndex != -1) {
            downloadUrl.substring(startIndex, endIndex)
        } else {
            downloadUrl.substring(startIndex)
        }

        return decodeFirebasePath(encodedPath)
    }

    private fun decodeFirebasePath(encodedPath: String): String {
        return encodedPath
            .replace("%2F", "/")
            .replace("%20", " ")
    }

    override fun readLastTenProducts(): Flow<RequestState<List<Product>>> = channelFlow {

        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection(collectionPath = "product")
                    .orderBy("createdAt", Direction.DESCENDING)
                    .limit(10)
                    .snapshots
                    .collectLatest { query ->
                        val products = query.documents.map { document ->
                            Product(
                                id = document.id,
                                title = document.get(field = "title"),
                                createdAt = document.get(field = "createdAt"),
                                description = document.get(field = "description"),
                                thumbnail = document.get(field = "thumbnail"),
                                category = document.get(field = "category"),
                                flavors = document.get(field = "flavors"),
                                weight = document.get(field = "weight"),
                                price = document.get(field = "price"),
                                isPopular = document.get(field = "isPopular"),
                                isDiscounted = document.get(field = "isDiscounted"),
                                isNew = document.get(field = "isNew")
                            )
                        }
                        send(RequestState.Success(data = products.map { it.copy(title = it.title.uppercase()) }))
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading the last 10 items from the database: ${e.message}"))
        }


    }

    override suspend fun readProductById(id: String): RequestState<Product> {

        return try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productDocument = database.collection(collectionPath = "product")
                    .document(id)
                    .get()
                if (productDocument.exists) {
                    val product = Product(
                        id = productDocument.id,
                        title = productDocument.get(field = "title"),
                        createdAt = productDocument.get(field = "createdAt"),
                        description = productDocument.get(field = "description"),
                        thumbnail = productDocument.get(field = "thumbnail"),
                        category = productDocument.get(field = "category"),
                        flavors = productDocument.get(field = "flavors"),
                        weight = productDocument.get(field = "weight"),
                        price = productDocument.get(field = "price"),
                        isPopular = productDocument.get(field = "isPopular"),
                        isDiscounted = productDocument.get(field = "isDiscounted"),
                        isNew = productDocument.get(field = "isNew")
                    )
                    RequestState.Success(product.copy(title = product.title.uppercase()))
                } else {
                    RequestState.Error("Selected product not found.")
                }
            } else {
                RequestState.Error("User is not available.")
            }
        } catch (e: Exception) {
            RequestState.Error("Error while reading a selected product: ${e.message}")
        }


    }

    override suspend fun updateProductThumbnail(
        productId: String,
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productCollection = database.collection(collectionPath = "product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()
                if (existingProduct.exists) {
                    productCollection.document(productId)
                        .updateFields { "thumbnail" to downloadUrl}
                    onSuccess()
                } else {
                    onError("Selected Product not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a thumbnail image: ${e.message}")
        }
    }

    override suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        try {


            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productCollection = database.collection(collectionPath = "product")
                val existingProduct = productCollection
                    .document(product.id)
                    .get()
                if (existingProduct.exists) {
                    productCollection.document(product.id)
                        .updateFields {
                            "title" to product.title.lowercase()         // No lowercase!
                            "description" to product.description
                            "price" to product.price
                            "createdAt" to product.createdAt
                            "thumbnail" to product.thumbnail
                            "category" to product.category
                            "flavors" to product.flavors
                            "weight" to product.weight
                            "isNew" to product.isNew
                            "isPopular" to product.isPopular
                            "isDiscounted" to product.isDiscounted
                        }
                    onSuccess()
                } else {
                    onError("Selected Product not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a thumbnail image: ${e.message}")
        }



    }

    override suspend fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productCollection = database.collection(collectionPath = "product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()
                if (existingProduct.exists) {
                    productCollection.document(productId)
                        .delete()
                    onSuccess()
                } else {
                    onError("Selected Product not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while deleting item : ${e.message}")
        }



    }

    override fun searchProductsByTitle(searchQuery: String): Flow<RequestState<List<Product>>> = channelFlow {

        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore

//                    val queryText = searchQuery.trim().lowercase()
//                    val endText = queryText + "\uf8ff"

                database.collection(collectionPath = "product")
//                        .orderBy("title")
//                        .startAt(queryText)
//                        .endAt(endText)
                    .snapshots
                    .collectLatest { query ->
                        val products = query.documents.map { document ->
                            Product(
                                id = document.id,
                                title = document.get(field = "title"),
                                createdAt = document.get(field = "createdAt"),
                                description = document.get(field = "description"),
                                thumbnail = document.get(field = "thumbnail"),
                                category = document.get(field = "category"),
                                flavors = document.get(field = "flavors"),
                                weight = document.get(field = "weight"),
                                price = document.get(field = "price"),
                                isPopular = document.get(field = "isPopular"),
                                isDiscounted = document.get(field = "isDiscounted"),
                                isNew = document.get(field = "isNew")
                            )
                        }
                        send(
                            RequestState.Success(
                                products
                                    .filter { it.title.contains(searchQuery) }
                                    .map { it.copy(title = it.title.uppercase()) }
                            )
                        )
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while searching products: ${e.message}"))
        }



    }

    override suspend fun uploadImage(
        fileName: String,
        byteArray: ByteArray
    ): String? {

        return try {
            // Access the client via the Factory object
            val bucket = SupabaseFactory.client.storage.from("NutrionAppImages")

            bucket.upload(fileName, byteArray) {
                upsert = true
            }

            // Return the public URL
            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }


    }


}