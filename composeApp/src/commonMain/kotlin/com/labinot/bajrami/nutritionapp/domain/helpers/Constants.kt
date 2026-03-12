package com.labinot.bajrami.nutritionapp.domain.helpers

object Constants {

    const val WEB_CLIENT_ID = "168975389588-ca7lcpfep09s8iviie4e0j43e5pnpctb.apps.googleusercontent.com"

    const val PAYPAL_CLIENT_ID = "AezlE-V4BVwRY0P8hZl5PZulPwPmhphs6flLmW8RoNa1R5eMjsiXPod6wWJXQbPpemovnFyqtlLrYfqe"
    const val PAYPAL_SECRET_ID = "EFunKFRC3ptVI2xQg2vzSt8a3ZWbEnkd7s97YWwBJXiw25UPb2b_RTdpKzZwqbJ4NWZ0hTm7zkHrEpFP"

    const val PAYPAL_AUTH_KEY = "$PAYPAL_CLIENT_ID:$PAYPAL_SECRET_ID"

    const val PAYPAL_AUTH_ENDPOINT = "https://api-m.sandbox.paypal.com/v1/oauth2/token"
    const val PAYPAL_CHECKOUT_ENDPOINT = "https://api-m.sandbox.paypal.com/v2/checkout/orders"

    const val RETURN_URL = "com.labinot.bajrami.nutritionapp://paypalpay?success=true"
    const val CANCEL_URL = "com.labinot.bajrami.nutritionapp://paypalpay?cancel=true"
     const val BASE_COUNTIES_API = "https://restcountries.com/v3.1/all"

    const val EMAIL_TRIGGER_KEY = "re_HwiRNkgj_QGqcXcLTiavFwPa2NUZFtSPf"

    const val MAX_QUANTITY = 10
    const val MIN_QUANTITY = 1


}