package com.labinot.bajrami.nutritionapp.domain.paymentProcess

import com.labinot.bajrami.nutritionapp.domain.helpers.Constants.EMAIL_TRIGGER_KEY
import com.labinot.bajrami.nutritionapp.domain.helpers.Constants.PAYPAL_AUTH_ENDPOINT
import com.labinot.bajrami.nutritionapp.domain.helpers.Constants.PAYPAL_AUTH_KEY
import com.labinot.bajrami.nutritionapp.domain.helpers.Constants.PAYPAL_CHECKOUT_ENDPOINT
import com.labinot.bajrami.nutritionapp.domain.models.CartItem
import com.labinot.bajrami.nutritionapp.domain.models.ResendEmailRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import openWebBrowser
import kotlin.io.encoding.Base64
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PaypalApi {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
        }
    }

    private val _accessToken = MutableStateFlow("")
    val accessToken: StateFlow<String> = _accessToken.asStateFlow()

    suspend fun fetchAccessToken(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ){


        try {
            val authKey = Base64.encode(PAYPAL_AUTH_KEY.encodeToByteArray())
            val response = client.post(urlString = PAYPAL_AUTH_ENDPOINT) {
                headers {
                    append(HttpHeaders.Authorization, "Basic $authKey")
                    append(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString()
                    )
                }
                setBody("grant_type=client_credentials")
            }

            if (response.status == HttpStatusCode.OK) {
                val tokenResponse = response.body<PayPalTokenResponse>()
                _accessToken.value = tokenResponse.accessToken
                onSuccess(tokenResponse.accessToken)
            } else {
                onError("Error while fetching an Access Token: ${response.status} -${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            onError("Error while fetching an Access Token: ${e.message}")
        }



    }


    @OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class)
    suspend fun beginCheckout(
        amount: Amount,
        fullName: String,
        shippingAddress: ShippingAddress,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (_accessToken.value.isEmpty()) {
            onError("Error while starting the checkout: Access Token is empty.")
            return
        }

        val uniqueId = Uuid.random().toHexString()
        val orderRequest = OrderRequest(
            purchaseUnits = listOf(
                PurchaseUnit(
                    referenceId = uniqueId,
                    amount = amount,
                    shipping = Shipping(
                        name = Name(fullName = fullName),
                        address = shippingAddress
                    )
                )
            )
        )

        val response = client.post(urlString = PAYPAL_CHECKOUT_ENDPOINT) {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${_accessToken.value}")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                append("PayPal-Request-Id", uniqueId)
            }
            setBody(orderRequest)
        }

        if (response.status == HttpStatusCode.OK) {
            val orderResponse = response.body<OrderResponse>()
            val payerLink = orderResponse.links.firstOrNull { it.rel == "payer-action" }?.href

            withContext(Dispatchers.Main) {
                handleUrl(
                    url = payerLink,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        } else {
            onError("Error while starting the checkout: ${response.status} - ${response.bodyAsText()}")
        }
    }

    private fun handleUrl(
        url: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (url == null) {
            onError("Error while opening a web browser: URL is null")
            return
        }

        openWebBrowser(url = url)
        onSuccess()
    }

    suspend fun sendOrderEmail(
        orderType: String,
        customerName: String,
        customerEmail: String,
        address: String,
        postalCode: String,
        phone: String,
        total: String,
        cartItems: List<CartItem>
    ) {


        val itemsHtml = cartItems.joinToString("") { item ->
            """
        <tr>
            <td style="padding: 8px; border-bottom: 1px solid #eee;">${item.productId}</td>
            <td style="padding: 8px; border-bottom: 1px solid #eee; text-align: center;">${item.quantity}</td>
            <td style="padding: 8px; border-bottom: 1px solid #eee; text-align: right;">$${item.flavor}</td>
        </tr>
        """.trimIndent()
        }

        // Create the Request Object
        val emailRequest = ResendEmailRequest(
            from = "NutritionApp <onboarding@resend.dev>",
            to = listOf("noti-002@hotmail.com"), // Your verified Resend login email
            subject = "New Order: $orderType - $customerName",
            html = """
            <div style="font-family: sans-serif; padding: 20px; color: #333; border: 1px solid #eee;">
                <h2 style="color: #2e7d32;">New Order Received!</h2>
                <p><strong>Method:</strong> $orderType</p>
                <p><strong>Customer:</strong> $customerName</p>
                <p><strong>Email:</strong> $customerEmail</p>
                <p><strong>Address:</strong> $address, $postalCode</p>
                <p><strong>Phone:</strong> $phone</p>
                
                <h3 style="margin-top: 20px;">Order Summary:</h3>
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="background-color: #f8f8f8;">
                            <th style="text-align: left; padding: 8px; border-bottom: 2px solid #ddd;">Item</th>
                            <th style="text-align: center; padding: 8px; border-bottom: 2px solid #ddd;">Qty</th>
                            <th style="text-align: right; padding: 8px; border-bottom: 2px solid #ddd;">Price</th>
                        </tr>
                    </thead>
                    <tbody>
                        $itemsHtml
                    </tbody>
                </table>
                
                <div style="text-align: right; margin-top: 20px;">
                    <h2 style="color: #000;">Total: $$total</h2>
                </div>
            </div>
        """.trimIndent()
        )

        try {
            val response = client.post("https://api.resend.com/emails") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $EMAIL_TRIGGER_KEY")
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(emailRequest) // Ktor now knows exactly how to serialize this
            }

            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                println("Email Sent Successfully!")
            } else {
                // This will help you see if there is still an API key issue
                val errorDetails = response.bodyAsText()
                println("Resend API Error: ${response.status} - $errorDetails")
            }
        } catch (e: Exception) {
            println("Network Exception: ${e.message}")
        }


    }

}